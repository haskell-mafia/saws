import sbt._
import Keys._

object depend {
  val scalaz = Seq(  "org.scalaz" %% "scalaz-core"   % "7.1.0"
                   , "org.scalaz" %% "scalaz-effect" % "7.1.0")

  val scalazStream = Seq("org.scalaz.stream" %% "scalaz-stream" % "0.5a")

  val mundaneVersion = "1.2.1-20141024050817-e2b01b2"

  val mundane = Seq(
      "com.ambiata" %% "mundane-io"
    , "com.ambiata" %% "mundane-control").map(_ % mundaneVersion)

  val mundaneTesting = Seq(
      "com.ambiata" %% "mundane-testing"   % mundaneVersion)

  val aws = Seq(
      "com.ambiata" %% "saws-aws"          % "1.2.1-20141114025437-6525723" intransitive()
    , "commons-logging"            % "commons-logging"     % "1.1.1"
    , "com.owtelse.codec"          % "base64"              % "1.0.6"
    , "javax.mail"                 % "mail"                % "1.4.7")

  val specs2Version = "2.4.5"

  val specs2 = Seq(
      "org.specs2" %% "specs2-core"        % specs2Version)

  val testing = Seq(
      "org.specs2" %% "specs2-core"        % specs2Version % "test"
    , "org.specs2" %% "specs2-junit"       % specs2Version % "test"
    , "org.specs2" %% "specs2-scalacheck"  % specs2Version % "test")

  val ssh = Seq("com.decodified" %% "scala-ssh" % "0.6.4")

  val resolvers = Seq(
      Resolver.sonatypeRepo("releases")
    , Resolver.typesafeRepo("releases")
    , "cloudera"              at "https://repository.cloudera.com/content/repositories/releases"
    , Resolver.url("ambiata-oss", new URL("https://ambiata-oss.s3.amazonaws.com"))(Resolver.ivyStylePatterns)
    , "Scalaz Bintray Repo"   at "http://dl.bintray.com/scalaz/releases"
    // For 2.11 version of scala-ssh only
    , "spray.io"              at "http://repo.spray.io")

}
