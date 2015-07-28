package words

import akka.actor.ActorSystem
import akka.http.javadsl.model.RequestEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, FormData}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import spray.json.{JsArray, JsValue}

import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.marshalling.PredefinedToEntityMarshallers.FormDataMarshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


/**
 * Created by joost1 on 28/07/15.
 */
object DbPedia {

  case class Character(name: String, abstrakt: String)

  private val query = """select distinct  ?character ?abstract
                |where {
                |?character <http://dbpedia.org/property/creator> <http://dbpedia.org/resource/William_Shakespeare>.
                |?character <http://dbpedia.org/ontology/abstract> ?abstract.
                |FILTER(langMatches(lang(?abstract), "EN"))
                |} LIMIT 100""".stripMargin

  def findCharacterAbstracts()(implicit system: ActorSystem, materializer: Materializer): Future[Seq[Character]] = {
    import system.dispatcher
    val map = Map("query" -> query,
      "format" -> "application/json")

    val form = FormData(map)

    val respFut = for {entity <- Marshal(form).to[RequestEntity]
                       response <- Http().singleRequest(HttpRequest(uri = "http://dbpedia.org/sparql",
                         method = HttpMethods.POST).withEntity(entity))
                       respEntity <- Unmarshal(response.entity).to[JsValue]
    } yield respEntity

    respFut.flatMap { resp =>
      resp.asJsObject.fields("results").asJsObject.fields("bindings") match {
        case array: JsArray => Future.successful(array.elements.map(parse))
        case _ => Future.failed(new RuntimeException("parse failure"))
      }
    }
  }

  private def parse(inp: JsValue): Character = {
    val char = inp.asJsObject
    val abstr = char.fields("abstract").asJsObject.fields("value").toString()
    val name = char.fields("character").asJsObject.fields("value").toString()
    Character(name = name, abstrakt = abstr)
  }
}
