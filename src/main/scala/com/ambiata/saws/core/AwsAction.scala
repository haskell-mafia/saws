package com.ambiata.saws
package core

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient
import com.amazonaws.services.s3.AmazonS3Client
import scalaz._, Scalaz._, \&/._

case class AwsAction[A, +B](unsafeRun: A => (Vector[AwsLog], AwsAttempt[B])) {
  def map[C](f: B => C): AwsAction[A, C] =
    flatMap[C](f andThen AwsAction.ok)

  def mapError(f: These[String, Throwable] => These[String, Throwable]): AwsAction[A, B] =
    AwsAction[A, B](a => run(a) match { case (log, att) => (log, att.mapError(f)) })

  def contramap[C](f: C => A): AwsAction[C, B] =
    AwsAction(c => run(f(c)))

  def flatMap[C](f: B => AwsAction[A, C]): AwsAction[A, C] =
    AwsAction[A, C](a => run(a) match {
      case (log, AwsAttempt(\/-(b))) =>
        f(b).run(a) match {
          case (log2, result) => (log ++ log2, result)
        }
      case (log, AwsAttempt(-\/(e))) =>
        (log, AwsAttempt(-\/(e)))
    }).safe

  def attempt[C](f: B => AwsAttempt[C]): AwsAction[A, C] =
    flatMap(f andThen AwsAction.attempt)

  def orElse[BB >: B](alt: => BB): AwsAction[A, BB] =
    AwsAction[A, BB](a => run(a) match {
      case (log, AwsAttempt.Ok(b))    => (log, AwsAttempt.ok(b))
      case (log, AwsAttempt.Error(_)) => (log, AwsAttempt.ok(alt))
    })

  def safe: AwsAction[A, B] =
    AwsAction(a => AwsAttempt.safe(unsafeRun(a)) match {
      case AwsAttempt(-\/(err)) => (Vector(), AwsAttempt(-\/(err)))
      case AwsAttempt(\/-(ok))  => ok
    })

  def retry(i: Int, lf: (Int, These[String, Throwable]) => Vector[AwsLog] = (_,_) => Vector()): AwsAction[A, B] =
    AwsAction[A, B](a => run(a) match {
      case (log, AwsAttempt.Ok(b))    => (log, AwsAttempt.ok(b))
      case (log, AwsAttempt.Error(e)) => if(i > 0) retry(i - 1, lf).unsafeRun(a) match {
        case (nlog, nattp) => (log ++ lf(i, e) ++ nlog, nattp)
      } else (log ++ lf(i, e), AwsAttempt.these(e))
    })

  def run(a: A): (Vector[AwsLog], AwsAttempt[B]) =
    safe.unsafeRun(a)

  def runS3(implicit ev: AmazonS3Client =:= A) =
    run(Clients.s3)
    // TODO: returning the S3 client will mean streams to S3 will not be closed automatically
    // need a better way then this:
    // val cli = Clients.s3
    // (cli, run(cli))

  def runEC2(implicit ev: AmazonEC2Client =:= A) =
    run(Clients.ec2)

  def runIAM(implicit ev: AmazonIdentityManagementClient =:= A) =
    run(Clients.iam)

  def runS3EC2(implicit ev: (AmazonS3Client, AmazonEC2Client) =:= A) =
    run(Clients.s3 -> Clients.ec2)

  def runEC2IAM(implicit ev: (AmazonEC2Client, AmazonIdentityManagementClient) =:= A) =
    run(Clients.ec2 -> Clients.iam)

  def runS3EC2IAM(implicit ev: (AmazonS3Client, AmazonEC2Client, AmazonIdentityManagementClient) =:= A) =
    run((Clients.s3, Clients.ec2, Clients.iam))

  def execute(a: A) =
    AwsAction.unlog(run(a))

  def executeS3(implicit ev: AmazonS3Client =:= A) =
    AwsAction.unlog(runS3)

  def executeEC2(implicit ev: AmazonEC2Client =:= A) =
    AwsAction.unlog(runEC2)

  def executeIAM(implicit ev: AmazonIdentityManagementClient =:= A) =
    AwsAction.unlog(runIAM)

  def executeS3EC2(implicit ev: (AmazonS3Client, AmazonEC2Client) =:= A) =
    AwsAction.unlog(runS3EC2)

  def executeEC2IAM(implicit ev: (AmazonEC2Client, AmazonIdentityManagementClient) =:= A) =
    AwsAction.unlog(runEC2IAM)

  def executeS3EC2IAM(implicit ev: (AmazonS3Client, AmazonEC2Client, AmazonIdentityManagementClient) =:= A) =
    AwsAction.unlog(runS3EC2IAM)
}

object AwsAction {
  def config[A]: AwsAction[A, A] =
    AwsAction(a => (Vector(), AwsAttempt.ok(a)))

  def ok[A, B](strict: B): AwsAction[A, B] =
    value(strict)

  def attempt[A, B](value: AwsAttempt[B]): AwsAction[A, B] =
    AwsAction(_ => (Vector(), value))

  def value[A, B](value: => B): AwsAction[A, B] =
    AwsAction(_ => (Vector(), AwsAttempt.ok(value)))

  def exception[A, B](t: Throwable): AwsAction[A, B] =
    AwsAction(_ => (Vector(), AwsAttempt.exception(t)))

  def fail[A, B](message: String): AwsAction[A, B] =
    AwsAction(_ => (Vector(), AwsAttempt.fail(message)))

  def error[A, B](message: String, t: Throwable): AwsAction[A, B] =
    AwsAction(_ => (Vector(), AwsAttempt.error(message, t)))

  def withClient[A, B](f: A => B): AwsAction[A, B] =
    config[A].map(f)

  def attemptWithClient[A, B](f: A => AwsAttempt[B]): AwsAction[A, B] =
    config[A].attempt(f)

  def log[A](message: AwsLog): AwsAction[A, Unit] =
    AwsAction(_ => (Vector(message), AwsAttempt.ok(())))

  def unlog[A](result: (Vector[AwsLog], AwsAttempt[A])): AwsAttempt[A] = result match {
    case (log, attempt) => attempt
  }

  implicit def AwsActionMonad[A]: Monad[({ type l[a] = AwsAction[A, a] })#l] =
    new Monad[({ type L[a] = AwsAction[A, a] })#L] {
      def point[B](v: => B) = AwsAction.ok(v)
      def bind[B, C](m: AwsAction[A, B])(f: B => AwsAction[A, C]) = m.flatMap(f)
    }
}

object S3Action {
  def apply[A](f: AmazonS3Client => A) =
    AwsAction.withClient(f)

  implicit def S3ActionMonad: Monad[S3Action] = AwsAction.AwsActionMonad[AmazonS3Client]
}

object EC2Action {
  def apply[A](f: AmazonEC2Client => A) =
    AwsAction.withClient(f)
}

object IAMAction {
  def apply[A](f: AmazonIdentityManagementClient => A) =
    AwsAction.withClient(f)
}
