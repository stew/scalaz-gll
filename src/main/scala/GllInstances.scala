package scalaz.gll

import com.codecommit.gll.Parsers.{Parser,TerminalParser}
import com.codecommit.gll.{LineStream,Success}
import scalaz.{Monoid, Monad}

trait ScalazInstances { self : com.codecommit.gll.Parsers =>

  implicit val gllMonad : Monad[Parser] = new Monad[Parser] {

      def point[A](a: => A) = new TerminalParser[A] {
          def computeFirst(s: Set[Parser[Any]]) = Some(Set(None))
          def parse(in: LineStream) = Success(a, in)
        }

      def bind[A,B](fa: Parser[A])(f: A=>Parser[B]) = fa flatMap f

      override def map[A,B](fa: Parser[A])(f: A=>B) = fa ^^ f
    }

  implicit def gllMonoid[A](implicit aMonoid: Monoid[A]) : Monoid[Parser[A]] = 
    new Monoid[Parser[A]] {
      def zero = new TerminalParser[A] {
          def computeFirst(s: Set[Parser[Any]]) = Some(Set(None))
          def parse(in: LineStream) = Success(aMonoid.zero, in)
        }

      def append(a: Parser[A], aa: => Parser[A]) = a ~ aa ^^ { aMonoid.append(_,_) }
    }
}
