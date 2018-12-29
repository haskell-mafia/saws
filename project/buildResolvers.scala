import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider}
import ohnosequences.sbt.SbtS3Resolver.autoImport.{s3 => ss33, _}
import sbt._

object buildResolvers {

  val sonaTypeReleases = Resolver.sonatypeRepo("releases")
  val typesafeReleases = Resolver.typesafeRepo("releases")
  val cloudera = new MavenRepository("cloudera", "https://repository.cloudera.com/content/repositories/releases")
  val ambiataOss = Resolver.url("ambiata-oss", new URL("https://ambiata-oss.s3.amazonaws.com"))(Resolver.ivyStylePatterns)
  def ambiataOssV2(r: Region, p: String) = s3Resolver(r, p)("ambiata-oss-v2", ss33("ambiata-oss-v2")).withIvyPatterns
  val scalaz = Resolver.bintrayRepo("scalaz", "releases")
  // For 2.11 version of scala-ssh only
  val spayRepo = "spray repo"            at "http://repo.spray.io"


  def s3Resolver(r: Region, p: String): (String, ss33) => S3Resolver = S3Resolver(
      new EnvironmentVariableCredentialsProvider() |
      new InstanceProfileCredentialsProvider() |
      new ProfileCredentialsProvider(p)
    , false
    , r
    , com.amazonaws.services.s3.model.CannedAccessControlList.BucketOwnerFullControl)

  def allResolvers(r: Region, p: String) = Seq[Resolver](
      sonaTypeReleases
    , typesafeReleases
    , cloudera
    , ambiataOss
    , ambiataOssV2(r, p)
    , scalaz
    , spayRepo
  )

}


