package words

import org.scalatest._
import Main.out

class ParsingSpec extends FlatSpec with Matchers {
  "Persona parser" should "ignore indefinite article" in {
    Personae.parsePersona("A Hero") should be(None)
  }

  it should "correctly identify the persona in a dramatis line" in {
    Personae.parsePersona("MACBETH, Thane of Glamis and Cawdor, a general in the King's army") should be(Some("MACBETH"))
  }

  it should "find existing persona" in {
    val lines=681
    Personae.scan(Shakespeare.source).size should be(lines)
  }

  "Processing" should "strip the initial and final license" in {
    import Processing._
    for (line <- Shakespeare.source.getLines().stripLicenses()) {
      line.toLowerCase.contains(" ebook") should be(false)
    }
  }

  "in memory processing" should "determine correct count for 'bequeath'" in {
    val occurrences=9
    InMemory.wordCount(Shakespeare.source.getLines())("bequeath") should be(occurrences)
  }

  "lazy processing" should "handle large amount of data in availabble memory" in {
    lazy val bigString = (0 to 100).map(_ => "What's in a name? That which we call a rose").mkString
    val it: Iterator[String] = new Iterator[String] {
      override def next(): String = bigString

      override def hasNext: Boolean = true
    }
    Runtime.getRuntime.gc()
    def free:Long = Runtime.getRuntime.freeMemory()
    val startMem = free
    val startTime= System.nanoTime()
    Lazy.wordCount(it.take(Int.MaxValue / 16))
    val endMem = free
    val endTime = System.nanoTime()
    val deltaMem = (startMem - endMem) / 1024 / 1024
    val deltaTime = (endTime-startTime)/1000/1000/1000
    out(s"processed in $deltaTime s using $deltaMem MB")
  }
}
