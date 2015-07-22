package words


import scala.io.Source
import Shakespeare._

object Processing {
  implicit class NoLicenseIterator(it:Iterator[String]) {
    /** strips the initial and final license.
  use Shakespeare.endOfInitialLicense and startOfFinalLicense
      */
    def stripLicenses():Iterator[String]= ???
  }

  def toWords(line:String):List[String]=line.split("\\W").toList
}

object InMemory{
  import Processing._
  /** takes a line iterator and returns a map of words and their count in the text.
    *
    * Process only lines starting from Shakespeare.endOfLicense and until Shakespeare.startOfFinalLicense.
    *
    * Use the function toWords to change a line into a list of words.
    * Use the function count
    */
  def wordCount(it:Iterator[String]):Map[String,Int]= ???

  /** takes a list of words and returns a map of words to their wordcount*/
  def count(words:List[String]):Map[String,Int]= {
    words.groupBy(_.toString).map{ case (w,words) => (w, words.size)}
  }
}

object Lazy {
  import Processing._

  /** takes a line iterator and returns a map of words and their count in the text.
    * The function operates lazily. That is; lines that aren't currently being processed are not loaded into memory yet.
    *
    * Process only lines starting from Shakespeare.endOfLicense and until Shakespeare.startOfFinalLicense.
    * Use the function toWords to change a line into a list of words.
    *
    * Use the function count to combine the incoming words into a single outcome.
    */
  def wordCount(it:Iterator[String]):Map[String,Int]= ???

  /** given the previous map from word to word count
    * and the words from the current line
    * returns the updated map from word to word count

    */
  def count(acc:Map[String,Int],words:List[String]):Map[String,Int]= ???
}

