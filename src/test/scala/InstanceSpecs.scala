package scalaz.gll

import org.specs2._
import org.specs2.matcher._
import scalaz.{Success=>Successz,_}
import Scalaz._

import com.codecommit.gll._

object GllInstancesSpec extends RegexParsers with Specification with ScalazInstances {

  val asdfP : Parser[String] = "asdf"
  val qwerP : Parser[String] = "qwer"

  case class Person(first: String, last: String)
  val nameP : Parser[String] = regex("[A-Za-z]*"r)
  val applicativePerson = (nameP |@| nameP)(Person)
  val applyPerson = ^(nameP, nameP)(Person)
  val monadPerson = nameP >>= { first => nameP.map(Person(first,_)) }

  def is = {
    "Parser" ^
    "is a semigroup" ! ((asdfP |+| qwerP)("asdfqwer") must parseSingle("asdfqwer")) ^
    "is pointed" ! ((1234.point[Parser] ~ asdfP)("asdf") must parseSingle(com.codecommit.gll.~(1234,"asdf"))) ^
    "has mzero" ! testMzero ^
    "is a monoid" ! testMonoidSum ^
    "is applicative" ! testPersonParser(applicativePerson) ^
    "and apply works" ! testPersonParser(applyPerson) ^
    "is a monad" ! testPersonParser(monadPerson) ^
    end
  }

  def parseSingle[A](expect: A) = new Matcher[Stream[Result[A]]] {
    def apply[S <: Stream[Result[A]]](s: Expectable[S]) = {
      println("s: " + s)
      s.value match {
        case Success(x, _) #:: Stream.Empty if expect.equals(x) => 
          result(true, expect.toString, expect.toString, s)
        case Success(x, _) #:: Stream.Empty => 
          result(false, expect.toString, x.toString, s)
        case Success(_, _) #:: x => 
          result(false, "a single result", "multiple results", s)

        case x => result(false, "success", x.toString, s)
      }
    }
  }  

  def testMzero =
      (∅[Parser[List[String]]] ~ asdfP)("asdf") match {
        case Success(Nil ~ "asdf", _) #:: Stream.Empty => success
        case x => failure
      }

  def testMonoidSum = {
    val sumP = List[Parser[String]]("a","b","c","d").suml
    sumP("abcd") must parseSingle("abcd")
  }

  def testPersonParser(personP: Parser[Person]) = 
    personP("Harvey McGilicuddy") must parseSingle(Person("Harvey", "McGilicuddy"))
}
