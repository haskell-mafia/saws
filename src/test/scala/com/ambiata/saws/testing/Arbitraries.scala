package com.ambiata
package saws
package testing

import com.ambiata.saws.core._
import org.scalacheck._, Arbitrary._
import scalaz._, Scalaz._
import scalaz.effect.IO
import mundane.control._
import mundane.testing.Arbitraries._

object Arbitraries {
  implicit def AwsLogArbitrary: Arbitrary[AwsLog] = Arbitrary(Gen.oneOf(
    arbitrary[String].map(AwsLog.CreateVPC),
    arbitrary[String].map(AwsLog.CreateInternetGateway),
    arbitrary[String].map(AwsLog.CreateRouteTable),
    arbitrary[String].map(AwsLog.CreateSubnet),
    arbitrary[String].map(AwsLog.CreateRole),
    arbitrary[String].map(AwsLog.CreateBucket),
    arbitrary[String].map(AwsLog.CreateInstanceProfile),
    arbitrary[String].map(AwsLog.CreateSecurityGroup),
    arbitrary[String].map(AwsLog.UpdateRole),
    arbitrary[String].map(AwsLog.UpdateBucket),
    arbitrary[String].map(AwsLog.UpdateInstanceProfile),
    arbitrary[String].map(AwsLog.UpdateSecurityGroup),
    arbitrary[String].map(AwsLog.StartInstance),
    arbitrary[String].map(AwsLog.StopInstance),
    arbitrary[String].map(AwsLog.Info),
    arbitrary[String].map(AwsLog.Warn),
    arbitrary[String].map(AwsLog.Debug)
  ))

  implicit def AwsIntArbitrary: Arbitrary[Aws[Int, Int]] =
    Arbitrary(for {
      base  <- arbitrary[Aws[Int, Int => Int]]
      m     <- arbitrary[Int]
    } yield base.map(_(m)))

  implicit def AwsFuncArbitrary: Arbitrary[Aws[Int, Int => Int]] =
    Arbitrary(for {
      logs  <- Gen.choose(0, 4).flatMap(n => Gen.listOfN(n, arbitrary[AwsLog]))
      f     <- func
      base  <- arbitrary[Result[Int]]
    } yield  Aws(
      ActionT(r =>
        ResultT[({ type l[+a] = WriterT[IO, Vector[AwsLog], a] })#l, Int => Int](
          WriterT[IO, Vector[AwsLog], Result[Int => Int]](
            IO { (logs.toVector, base.map(n => f(n))) })))))

  def func: Gen[Int => Int => Int] = arbitrary[Int].flatMap(x => Gen.oneOf(
    (m: Int) => (n: Int) => n,
    (m: Int) => (n: Int) => m,
    (m: Int) => (n: Int) => x,
    (m: Int) => (n: Int) => n * m * x,
    (m: Int) => (n: Int) => n * m,
    (m: Int) => (n: Int) => n * x,
    (m: Int) => (n: Int) => m * x
  ))

}
