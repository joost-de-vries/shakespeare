package words
import scala.io.Source._

/**
 * Created by joost1 on 22/07/15.
 */
object Shakespeare{
  def source = fromInputStream(getClass.getResourceAsStream("/shakespeare - works.txt"))

  val endOfInitialLicense = 170
  
  val startOfFinalLicense = 124452

  def isDramatisStart(line: String): Boolean =
    line.toLowerCase.startsWith("dramatis personae")


  def isDramatisEnd(line:String):Boolean =
    line.startsWith("<<THIS ELECTRONIC VERSION")

}
