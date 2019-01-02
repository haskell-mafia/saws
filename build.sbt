import com.amazonaws.services.s3.model.Region
import com.ambiata.promulgate.project.ProjectPlugin._
import ohnosequences.sbt.SbtS3Resolver.autoImport.{awsProfile, s3region}
import sbt.Def
import buildResolvers.allResolvers
import com.amazonaws.auth.{EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.ambiata.promulgate.project.Sett

lazy val ossBucket: String =
  sys.env.getOrElse("AMBIATA_IVY_OSS", "ambiata-oss")

lazy val standardSettings = Defaults.coreDefaultSettings ++
                 projectSettings          ++
                 compilationSettings      ++
                 testingSettings

lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    organization := "com.ambiata"
  , version in ThisBuild := "1.4.1"
  , scalaVersion := "2.11.6"
  , crossScalaVersions := Seq(scalaVersion.value)
  , publishArtifact in (Test, packageBin) := true
  , awsProfile := "default"
  , s3region := Region.AP_Sydney
  , s3credentials := new EnvironmentVariableCredentialsProvider() |
    InstanceProfileCredentialsProvider.getInstance() |
    new ProfileCredentialsProvider(awsProfile.value)
  , resolvers ++= allResolvers.value
  , libraryDependencies ++= depend.awsDeps
) ++ Seq(prompt)

lazy val saws = (project in file("."))
  .settings(
    name := "saws"
    , standardSettings
    , promulgate.library("com.ambiata.saws", ossBucket)
  )
  .aggregate(core, ec2, s3, iam, emr, ses, cw, testing)
  .dependsOn(core, ec2, s3, iam, emr, ses, cw)

lazy val core = (project in file("saws-core"))
  .settings(
      moduleName := "saws-core"
    , standardSettings
    , packageSettings
    , libraryDependencies ++= depend.awsLib("core") ++ depend.awsLib("iam") ++ depend.awsLib("ec2") ++
        depend.awsLib("s3") ++ depend.awsLib("emr") ++ depend.awsLib("ses") ++ depend.awsLib("cloudwatch") ++
        depend.scalaz ++ depend.mundane ++ depend.testing
  )

lazy val iam = (project in file("saws-iam"))
  .settings(
      moduleName := "saws-iam"
    , packageSettings
    , libraryDependencies ++= depend.awsLib("iam") ++ depend.testing
  )
  .dependsOn(core)

lazy val ec2 = (project in file("saws-ec2"))
  .settings(
      moduleName := "saws-ec2"
    , packageSettings
    , libraryDependencies ++= depend.awsLib("ec2")
  )
  .dependsOn(core, iam)

lazy val s3 = (project in file("saws-s3"))
  .settings(
      moduleName := "saws-s3"
    , packageSettings
    , libraryDependencies ++= depend.awsLib("s3")
  )
  .dependsOn(core)

lazy val emr = (project in file("saws-emr"))
  .settings(
      moduleName := "saws-emr"
    , packageSettings
    , libraryDependencies ++= depend.awsLib("emr")
  )
  .dependsOn(core)

lazy val ses = (project in file("saws-ses"))
  .settings(
      moduleName := "saws-ses"
    , packageSettings
    , libraryDependencies ++= depend.awsLib("ses")
  )
  .dependsOn(core)

lazy val cw = (project in file("saws-cw"))
  .settings(
      moduleName := "saws-cw"
    , packageSettings
    , libraryDependencies ++= depend.awsLib("cloudwatch") ++ depend.scalaz ++ depend.testing ++ depend.disorder ++ depend.mundaneTesting
  )
  .dependsOn(core)

lazy val testing = (project in file("saws-testing"))
  .settings(
      moduleName := "saws-testing"
    , packageSettings
    , libraryDependencies ++= depend.awsLib("test-utils") ++ depend.specs2 ++ depend.ssh ++ depend.mundane ++ depend.mundaneTesting ++ depend.disorder
  )
  .dependsOn(iam, emr, ec2, ses, s3, cw, cw % "test->test")

lazy val compilationSettings: Seq[Def.Setting[_]] = Seq(
  javacOptions ++= Seq("-Xmx3G", "-Xms512m", "-Xss4m"),
  maxErrors := 10,
  scalacOptions ++= Seq("-feature", "-language:_"),
  scalacOptions in Compile ++= Seq(
    "-target:jvm-1.6"
  , "-deprecation"
  , "-unchecked"
  , "-feature"
  , "-language:_"
  , "-Ywarn-value-discard"
  , "-Yno-adapted-args"
  , "-Xlint"
//    , "-Xfatal-warnings" // too painful to fix as my scala knowledge is poor
  , "-Yinline-warnings"),
  scalacOptions in Test ++= Seq("-Yrangepos")
)

def packageSettings: Seq[Def.Setting[_]] =
  standardSettings ++ library(ossBucket)

def library(bucket: String): Seq[Sett] =
  promulgateVersionSettings ++
    promulgateNotifySettings ++
    promulgateSourceSettings ++
    promulgateS3LibSettings ++ Seq(
    BuildInfoKeys.pkg := organization.value + "." + moduleName.value
    , S3LibKeys.bucket := bucket
  )

lazy val testingSettings: Seq[Def.Setting[_]] = Seq(
    initialCommands in console := "import org.specs2._"
  , logBuffered := false
  , cancelable := true
  , javaOptions += "-Xmx3G"
  , fork in Test := Option(System.getenv("NO_FORK")).map(_ != "true").getOrElse(true)
  , testOptions in Test ++= (if (Option(System.getenv("FORCE_AWS")).isDefined || Option(System.getenv("AWS_ACCESS_KEY")).isDefined)
                               Seq()
                             else
                               Seq(Tests.Argument("--", "exclude", "aws")))
)

lazy val prompt = shellPrompt in ThisBuild := { state =>
  val name = Project.extract(state).currentRef.project
  (if (name == "saws") "" else name) + "> "
}
