package words

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.http.javadsl.model.RequestEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import spray.json.{JsObject, JsArray, JsValue}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scala.util.{Success, Failure}

object Main extends App {

  //    for((word,count) <- Lazy.wordCount(Shakespeare.source.getLines())){
  //      println(s"$word : $count")
  //    }

  //InMemory.wordCount(Shakespeare.source.getLines()).foreach(wc => println(wc))
  val start = System.nanoTime()

  //Personae.scan(Shakespeare.source).foreach(println)

  import Shakespeare._

  //  val futures=readFile()
  ////val x = futures.map(_.map(print))
  //  val f=Future.sequence(futures)
  //  Await.result(f,5000.milliseconds)
  //  val end = System.nanoTime()
  //  println((end-start)/1000000)

  implicit val system = ActorSystem("wordsSystem")
  implicit val materializer = ActorMaterializer()

  //  val stream = readStream()
  //  val sink = Sink.foreach(print)
  //  stream.runWith(sink)

  val x = Await.result(DbPedia.findCharacterAbstracts(), 1000.millis)

  println(x)

}
