package words

import org.scalatest._
import words.Main.out

class MappingSpec extends FlatSpec with Matchers {
  "Util find" should "ignore indefinite article" in {
    Util.find(1) should be(Some(List(Product(1,"tv"),Product(2,"cd player"))))
  }

  it should "find None" in {
    Util.find(4) should be(None)
  }

  
}
