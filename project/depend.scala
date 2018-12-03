import sbt.Keys.libraryDependencies
import sbt._

object depend {
  val scalaz = Seq(  "org.scalaz" %% "scalaz-core"   % "7.1.0"
                   , "org.scalaz" %% "scalaz-effect" % "7.1.0")

  val mundaneVersion = "1.2.1-20160108044905-83acfd2"
  val mundane = Seq(
      "com.ambiata" %% "mundane-io"
    , "com.ambiata" %% "mundane-control").map(_ % mundaneVersion) ++ Seq(
      "com.ambiata" %% "mundane-io" % mundaneVersion % "test->test")

  val mundaneTesting = Seq(
      "com.ambiata" %% "mundane-testing"   % mundaneVersion % "test->test")

  val disorder =
    Seq("com.ambiata" %% "disorder" % "0.0.1-20150824025853-fa03215" % "test")

  val awsVersion = "1.11.357"
  val aws     = Seq(
      "com.amazonaws"              % "aws-java-sdk"        % awsVersion
  )

  val awsDeps     = Seq(
      "commons-logging"            % "commons-logging"     % "1.1.1"
    , "com.owtelse.codec"          % "base64"              % "1.0.6"
    , "javax.mail"                 % "mail"                % "1.4.7")

  def awsLib(name: String) = Seq(
    "com.amazonaws" % s"aws-java-sdk-$name" % awsVersion)

  val specs2Version = "2.4.5"

  val specs2 = Seq(
      "org.specs2" %% "specs2-core"        % specs2Version)

  val testing = Seq(
      "org.specs2" %% "specs2-core"        % specs2Version % "test"
    , "org.specs2" %% "specs2-junit"       % specs2Version % "test"
    , "org.specs2" %% "specs2-scalacheck"  % specs2Version % "test")

  val ssh = Seq("com.decodified" %% "scala-ssh" % "0.6.4")

}
