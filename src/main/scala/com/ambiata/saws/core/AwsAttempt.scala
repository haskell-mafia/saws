package com.ambiata.saws
package core

import scala.util.control.NonFatal
import scalaz._, Scalaz._, \&/._

case class AwsAttempt[+A](run: These[String, Throwable] \/ A) {
  def map[B](f: A => B): AwsAttempt[B] =
    AwsAttempt(run.map(f))

  def flatMap[B](f: A => AwsAttempt[B]): AwsAttempt[B] =
    AwsAttempt(run.flatMap(a => f(a).run))

  def mapError(f: These[String, Throwable] => These[String, Throwable]): AwsAttempt[A] =
    AwsAttempt(run.leftMap(f))

  def isOk =
    run.isRight

  def isError =
    run.isLeft

  def toOption =
    run.toOption

  def toEither =
    run.toEither

  def toOptionError =
    run.swap.toOption

  def toOptionErrorMessage =
    run.swap.toOption.map(AwsAttempt.asString)

  def toDisjunction =
    run

  def getOrElse[AA >: A](otherwise: => AA): AA =
    toOption.getOrElse(otherwise)

  def |||[AA >: A](otherwise: => AwsAttempt[AA]): AwsAttempt[AA] =
    if (isOk) this else otherwise
}

object AwsAttempt {
  object Ok {
    def unapply[A](attempt: AwsAttempt[A]) =
      attempt.toOption
  }

  object Error {
    def unapply[A](attempt: AwsAttempt[A]) =
      attempt.toOptionError
  }

  object ErrorMessage {
    def unapply[A](attempt: AwsAttempt[A]) =
      attempt.toOptionErrorMessage
  }

  def safe[A](thunk: => A): AwsAttempt[A] =
    try ok(thunk) catch { case NonFatal(t) => exception(t) }

  def ok[A](value: A): AwsAttempt[A] =
    AwsAttempt(value.right)

  def exception[A](t: Throwable): AwsAttempt[A] =
    AwsAttempt(That(t).left)

  def fail[A](message: String): AwsAttempt[A] =
    AwsAttempt(This(message).left)

  def error[A](message: String, t: Throwable): AwsAttempt[A] =
    these(Both(message, t))

  def these[A](both: These[String, Throwable]): AwsAttempt[A] =
    AwsAttempt(both.left)

  def asString(these: These[String, Throwable]) = these match {
    case (This(x)) => x
    case (That(x)) => x.toString()
    case (Both(x, _)) => x
  }

  def prependThis(these: These[String, Throwable], prepend: String): These[String, Throwable] =
    these.fold(m      => This(prepend + " - " + m),
               t      => Both(prepend, t),
               (m, t) => Both(prepend + " - " + m, t))

  implicit def AwsAttemptMonad: Monad[AwsAttempt] = new Monad[AwsAttempt] {
    def point[A](v: => A) = ok(v)
    def bind[A, B](m: AwsAttempt[A])(f: A => AwsAttempt[B]) = m.flatMap(f)
  }

  implicit def AwsAttemptEqual[A: Equal]: Equal[AwsAttempt[A]] = {
    implicit def ThrowableEqual = Equal.equalA[Throwable]
    implicitly[Equal[These[String, Throwable] \/ A]].contramap(_.run)
  }
}
