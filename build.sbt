val catsV = "2.1.1"
val catsEffectV = "2.1.2"
val http4sV = "0.21.1"
val fs2V = "2.2.2"
val awsSdkV = "1.11.739"
val scalatestV = "3.1.1"
val scalacheckV = "1.14.3"
val scalatestScalacheckV = "3.1.1.1"
val log4jV = "2.13.1"

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / crossScalaVersions += "2.12.10"
ThisBuild / organization := "com.github.fd4s"
ThisBuild / organizationName := "Fd4s"

lazy val http4sAws = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "http4s-aws",
    licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    startYear := Some(2019),
    homepage := Some(url("https://github.com/filosganga/http4s-aws")),
    scmInfo := Some(ScmInfo(url("https://github.com/filosganga/http4s-aws"), "git@github.com:filosganga/http4s-aws.git")),
    developers := List(
      Developer(
        id = "filosganga",
        name = "Filippo De Luca",
        email = "me@filippodeluca.com",
        url = url("https://fillippodeluca.com")
      )
    ),
    headerLicense := Some(
      de.heikoseeberger.sbtheader.License.ALv2(
        s"${startYear.value.get}-${java.time.Year.now}",
        "fd4s",
        HeaderLicenseStyle.SpdxSyntax
      )
    ),
    releaseEarlyWith := BintrayPublisher,
    releaseEarlyEnableSyncToMaven := false,
    releaseEarlyNoGpg := true,
    //  releaseEarlyEnableSyncToMaven := false,
    bintrayOrganization := Some("filosganga"),
    bintrayRepository := "maven",
    bintrayPackageLabels := Seq(
      "aws",
      "cats",
      "cats-effect",
      "http4s",
      "fs2",
      "scala",
    ),
    version ~= (_.replace('+', '-')),
    dynver ~= (_.replace('+', '-')),
    buildInfoPackage := "com.github.fd4s.http4s.aws",
    // enable all options from sbt-tpolecat except fatal warnings
    scalacOptions -= "-Xfatal-warnings", 
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    libraryDependencies ++= List(
      "org.typelevel" %% "cats-core" % catsV,
      "org.typelevel" %% "cats-effect" % catsEffectV,
      "co.fs2" %% "fs2-core" % fs2V,
      "co.fs2" %% "fs2-io" % fs2V,
      "org.http4s" %% "http4s-client" % http4sV,

      "org.http4s" %% "http4s-blaze-client" % http4sV % Optional,
      "com.amazonaws" % "aws-java-sdk-core" % awsSdkV % Optional,

      "org.scalatest" %% "scalatest" % scalatestV % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.3" %  Test,
      "org.scalatestplus" %% "scalacheck-1-14" % scalatestScalacheckV %  Test,
      "org.apache.logging.log4j" % "log4j-api" % log4jV % Test,
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jV % Test,
      // TODO Get rid of it
      "commons-codec" % "commons-codec" % "1.14"
    )
  )
