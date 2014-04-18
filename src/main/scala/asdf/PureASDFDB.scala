package asdf

import scalaz.{Free, \/, \/-, -\/}
import scalaz.Free._
import scalaz.syntax.monad._
import scalaz.syntax.std.boolean._
import scalaz.syntax.std.option._
import scala.annotation.tailrec

import ASDF._
import ASDFDB._
import Path._
import Err._

object PureASDFDB {
  @tailrec def runPure[A](db: ASDF)(program: Program[A]): Err \/ A = {
    program match {
      case Return(a) => ???

      case Suspend(FindOne(path, cont)) => 
	runPure(??? : ASDF)(??? : Program[A])

      case Suspend(Insert(path, value, cont)) => ???
        
      case Gosub(fa, cont) => 
        // we have to fully evaluate fa before continuing
        fa() match {
          case Return(a) => ???
          case Suspend(Insert(path, value, cont0)) => ???
          case Suspend(FindOne(path, cont0)) => ???
          case Gosub(fa0, cont0) => ???
        }
    }
  }

  //
  // The following methods are provided for your convenience in the implementation
  // of the ??? portions above. 
  //

  /**
   * This method provides an implementation of insertion into an ASDF document
   * that will fail if a value already exists at the specified path, or if
   * the specified path conflicts with the document into which the value is
   * being inserted; for example, if a path indicates the presence of an ASeq
   * where there is already a primitive value or an ADict.
   *
   * For ease of * implementation, this is not trampolined so it will fail on 
   * large trees.
   */
  private def insert(into: ASDF, at: Path, value: ASDF): Err \/ ASDF = {
    def rec(trace: List[Path.Element], path: Path, db: ASDF): Err \/ ASDF = {
      (path, db) match {
        case (Path(Nil), v) => \/.right(v)

        case (Path((fld @ Field(n)) :: xs), ADict(d, meta)) => 
          d.get(n) match {
            case Some(child) =>
              rec(fld :: trace, Path(xs), child) map { v =>
                aDict(d + (n -> v), meta)
              }

            case None => 
              for {
                v <- xs.headOption match {
                  case Some(Field(_)) => rec(fld :: trace, Path(xs), ADict.empty())
                  case Some(Index(_)) => rec(fld :: trace, Path(xs), ASeq.empty())
                  case None => \/.right(value)
                }
              } yield {
                aDict(d + (n -> v), meta)
              }
          }

        case (Path((idx @ Index(i)) :: xs), ASeq(vx, meta)) => 
          val (prefix, suffix) = vx.splitAt(i)
          if (prefix.size < i) {
            \/.left(pathMismatch(Path((idx :: trace).reverse), AType.Value, AType.Missing))
          } else {
            suffix.headOption match {
              case Some(child) =>
                rec(idx :: trace, Path(xs), child) map { v =>
                  aSeq(prefix ++: v +: suffix.drop(1), meta)
                }

              case None => 
                for {
                  v <- xs.headOption match {
                    case Some(Field(_)) => rec(idx :: trace, Path(xs), ADict.empty())
                    case Some(Index(_)) => rec(idx :: trace, Path(xs), ASeq.empty())
                    case None => \/.right(value)
                  }
                } yield {
                  aSeq(prefix ++: v +: suffix.drop(1), meta)
                }
            }
          }

        case (Path(x :: xs), v) => 
          \/.left(pathMismatch(Path((x :: trace).reverse), x.atype, AType.forValue(v)))
      }
    }

    rec(Nil, at, into)
  }

  /**
   * A simple recursive traversal of the tree in the Option monad. For ease of
   * implementation, this is not trampolined so it will fail on large trees.
   */ 
  private def findOne(at: Path, in: ASDF): Option[ASDF] = (at, in) match {
    case (Path(Nil), v) => Some(v)
    case (Path(Index(i) :: xs), ASeq(v, _)) => v.lift(i) flatMap { findOne(Path(xs), _) }
    case (Path(Field(n) :: xs), ADict(m, _)) => m.get(n) flatMap { v => findOne(Path(xs), v) }
    case _ => None
  }
}


