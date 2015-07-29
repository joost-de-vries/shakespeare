package words

import akka.actor.ActorSystem
import akka.http.javadsl.model.RequestEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.marshalling.PredefinedToEntityMarshallers.FormDataMarshaller
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.Materializer
import spray.json.{JsArray, JsValue}

import scala.concurrent.{ExecutionContext, Future}


/**
 * Query dbpedia. Dbpedia is a semantic database with wikipedia content.
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

    val respFut: Future[JsValue] = toResponse(form)

    toCharacters(respFut)
  }

  //???
  import DbPediaInvoker._
  private def toResponse(form: FormData)(implicit system: ActorSystem, materializer: Materializer): Future[JsValue] = {
    import system.dispatcher
    for {entity <- createEntity(form)
                       response <- invoke(entity)
                       jsResult <- parseJsResult(response)
    } yield jsResult
  }

  //???
  private def toCharacters(respFut: Future[JsValue])(implicit executionContext: ExecutionContext): Future[Vector[Character]] = {
    respFut.map(toJsCharacters).flatMap {
      case array: JsArray => Future.successful(array.elements.map(toCharacter))
      case _ => Future.failed(new scala.RuntimeException("parse failure"))
    }
  }

  private def toJsCharacters(jsResponse: JsValue): JsValue = jsResponse.asJsObject.fields("results").asJsObject.fields("bindings")

  private def toCharacter(inp: JsValue): Character = {
    val char = inp.asJsObject
    val abstr = char.fields("abstract").asJsObject.fields("value").toString()
    val name = char.fields("character").asJsObject.fields("value").toString()
    Character(name = name, abstrakt = abstr)
  }
}

object DbPediaInvoker extends SprayJsonSupport{
  def createEntity(form: FormData)(implicit executionContext: ExecutionContext): Future[RequestEntity] = Marshal(form).to[RequestEntity]

  def invoke(entity: RequestEntity)(implicit system: ActorSystem, materializer: Materializer): Future[HttpResponse] = {
    Http().singleRequest(HttpRequest(uri = "http://dbpedia.org/sparql", method = HttpMethods.POST).withEntity(entity))
  }

  def parseJsResult(response: HttpResponse)
                   (implicit executionContext: ExecutionContext,um:Unmarshaller[ResponseEntity,JsValue]): Future[JsValue] = {
    Unmarshal(response.entity).to[JsValue]
  }
}
