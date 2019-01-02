import ohnosequences.sbt.SbtS3Resolver.autoImport._
import sbt.{Def, _}

object buildResolvers {

  val sonaTypeReleases = Resolver.sonatypeRepo("releases")
  val typesafeReleases = Resolver.typesafeRepo("releases")
  val cloudera = new MavenRepository("cloudera", "https://repository.cloudera.com/content/repositories/releases")
  val ambiataOss = Resolver.url("ambiata-oss", new URL("https://ambiata-oss.s3.amazonaws.com"))(Resolver.ivyStylePatterns)
  val scalaz = Resolver.bintrayRepo("scalaz", "releases")
  // For 2.11 version of scala-ssh only
  val spayRepo = "spray repo"            at "http://repo.spray.io"

  def allResolvers: Def.Initialize[Seq[sbt.Resolver]] =
    Def.setting {
      Seq(
        sonaTypeReleases
        , typesafeReleases
        , cloudera
        , ambiataOss
        , s3resolver.value("ambiata-oss-v2", s3("ambiata-oss-v2")).withIvyPatterns
        , scalaz
        , spayRepo
      )
    }
}


