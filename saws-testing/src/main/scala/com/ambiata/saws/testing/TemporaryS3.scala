package com.ambiata.saws.testing

import java.util.UUID

import com.ambiata.mundane.control._
import com.ambiata.saws.core.Clients
import com.ambiata.saws.s3._

import scalaz._, Scalaz._, effect._

case class TemporaryS3(s3: S3Address) {
  def clean: ResultT[IO, Unit] =
    S3.deleteAll(s3).executeT(Clients.s3)
}

object TemporaryS3 {
  implicit val TemporaryS3Resource = new Resource[TemporaryS3] {
    def close(temp: TemporaryS3) = temp.clean.run.void // Squelch errors
  }

  def withS3Address[A](f: S3Address => ResultTIO[A]): ResultTIO[A] =
    runWithS3Address(S3Address(testBucket, s3TempPath))(f)

  def runWithS3Address[A](s3: S3Address)(f: S3Address => ResultTIO[A]): ResultTIO[A] =
    ResultT.using(TemporaryS3(s3).pure[ResultTIO])(tmp => f(tmp.s3))

  def testBucket: String = Option(System.getenv("AWS_TEST_BUCKET")).getOrElse("ambiata-dev-view")

  def s3TempPath: String = s"tests/temporary-${UUID.randomUUID()}"
}
