package words

import java.nio.charset.StandardCharsets

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main extends App {

  //    for((word,count) <- Lazy.wordCount(Shakespeare.source.getLines())){
  //      println(s"$word : $count")
  //    }

  //InMemory.wordCount(Shakespeare.source.getLines()).foreach(wc => println(wc))
  val start = System.nanoTime()

      //Personae.scan(Shakespeare.source).foreach(println)
  import Shakespeare._
  val futures=readFile()
//val x = futures.map(_.map(print))
  val f=Future.sequence(futures)
  Await.result(f,5000.milliseconds)
  val end = System.nanoTime()
  println((end-start)/1000000)
}
