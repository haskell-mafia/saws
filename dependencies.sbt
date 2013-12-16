libraryDependencies ++= Seq(
    "org.scalaz"          %% "scalaz-core"                % "7.0.4",
    "com.github.scopt"    %% "scopt"                      % "3.1.0",
    "com.amazonaws"       %  "aws-java-sdk"               % "1.6.1",
    "com.ambiata"         %% "mundane"                    % "1.2.1-20131216003902-1dbd9d5",
    "com.owtelse.codec"   %  "base64"                     % "1.0.6",
    "org.specs2"          %% "specs2-matcher"             % "2.3.4" % "optional")

libraryDependencies ++= Seq(
    "org.specs2"          %% "specs2-core"                % "2.3.4"        % "test",
    "org.specs2"          %% "specs2-junit"               % "2.3.4"        % "test",
    "org.specs2"          %% "specs2-scalacheck"          % "2.3.4"        % "test",
    "com.decodified"      %% "scala-ssh"                  % "0.6.4"        % "test",
    "org.scalacheck"      %% "scalacheck"                 % "1.11.1"       % "test",
    "com.ambiata"         %% "scrutiny"                   % "1.0"          % "test")


resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.typesafeRepo("releases"),
    "cloudera"              at "https://repository.cloudera.com/content/repositories/releases",
    "spray"                 at "http://repo.spray.io",
    "artifactory"           at "http://etd-packaging.research.nicta.com.au/artifactory/libs-release-local",
    "artifactory snapshot"  at "http://etd-packaging.research.nicta.com.au/artifactory/libs-snapshot-local")
