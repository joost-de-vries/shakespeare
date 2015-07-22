package words

object Main extends App {

    //    for((word,count) <- Lazy.wordCount(Shakespeare.source.getLines())){
    //      println(s"$word : $count")
    //    }

    InMemory.wordCount(Shakespeare.source.getLines()).foreach(wc => println(wc))

    //    Personae.scan(Shakespeare.source.getLines()).foreach(println)
}
