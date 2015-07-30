package words

import scala.concurrent.Future

case class Customer(id:Long,name:String)

case class Address(street:String)

object CustomerDb {
  private val customers = List(Customer(0,"Erwin"),Customer(1,"Jochem"),Customer(2,"Elena"),Customer(3,"Sridar"))

  def findCustomerOpt(id:Long):Option[Customer] = customers.find(_.id==id)

  /** a fake asynchronous function to find a customer. We ignore the case were we can't find the customer. I.e. we're not using Option for that. */
  def findCustomerAsync(id:Long):Future[Customer] = findCustomerOpt(id).map(Future.successful(_)).getOrElse(Future.failed(new NullPointerException))

  /** fake asynchronous find of address */
  def findAddressAsync(customerName:String):Future[Address]=Future.successful(Address("Dorpstraat"))


}

case class Product(id:Long,name:String)

object ContractDb {
  private val products = Map("Erwin"->List(Product(0,"radio")),"Jochem"-> List(Product(1,"tv"),Product(2,"cd player")))

  def findProductOpt(customerName:String):Option[List[Product]]= products.get(customerName)

  def findProductAsync(customerName:String):Future[List[Product]] = findProductOpt(customerName).map(Future.successful(_)).getOrElse(Future.failed(new NullPointerException))

}

object Util{
  /** 4) implement the function to find the list of products if they exist */
  def find(customerId:Long):Option[List[Product]]= ???

    /** 5) implement an asynchronous function that returns the address and list of addresses,
      * invoking the findAddressAsync and findProductAsync in parallel. */
  def findAsync(customerId:Long):Future[(Address,List[Product])] = ???
}