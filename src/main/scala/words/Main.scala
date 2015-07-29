package words

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn.readLine

object Main extends App {

  //    for((word,count) <- Lazy.wordCount(Shakespeare.source.getLines())){
  //      out(s"$word : $count")
  //    }

  //InMemory.wordCount(Shakespeare.source.getLines()).foreach(wc => out(wc))
  //val start = System.nanoTime()

  //Personae.scan(Shakespeare.source).foreach(out)

  //  val futures=readFile()
  ////val x = futures.map(_.map(print))
  //  val f=Future.sequence(futures)
  //  Await.result(f,5000.milliseconds)
  //  val end = System.nanoTime()
  //  out((end-start)/1000000)

  implicit val system = ActorSystem("wordsSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  //  val stream = readStream()
  //  val sink = Sink.foreach(print)
  //  stream.runWith(sink)

//  val x = Await.result(DbPedia.findCharacterAbstracts(), 1000.millis)
//
//  out(x)

  val p =Shakespeare.readLines.map(_.map(out))
  Await.result(p,1000.millis)
  out("type ENTER to exit.")
  readLine
  system.shutdown()

  def out(s : Any) : Unit = println(s) //scalastyle:ignore regex

}
