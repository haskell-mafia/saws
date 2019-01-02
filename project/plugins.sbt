scalacOptions += "-deprecation"

resolvers += Resolver.url("ambiata-oss", new URL("https://ambiata-oss.s3.amazonaws.com"))(Resolver.ivyStylePatterns)
resolvers += Resolver.url("ambiata-oss-v2", new URL("https://ambiata-oss-v2.s3-ap-southeast-2.amazonaws.com"))(Resolver.ivyStylePatterns)

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.typesafeRepo("releases")

resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"

addSbtPlugin("com.ambiata" % "promulgate" % "0.12.0-20190102000029-19db212")

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.5")

addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.16.0")
