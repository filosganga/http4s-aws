# AWS Signature V4 implementation for http4s

Provides an http4s client middleware that authenticates requests with AWS Auth Signature V4.

## Getting started

To get started with sbt, simply add the following line to your build.sbt file.

````sbt
libraryDependencies += "com.github.fd4s" %% "http4s-aws" % "<version>"
````

Example of use with S3:

````scala

  import scala.concurrent.ExecutionContext.{global => ec}
  
  import cats.implicits._
  import cats.effect._
  
  import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

  import org.http4s.{Service => _, _}
  import org.http4s.Method._
  import org.http4s.client._
  import org.http4s.client.dsl.io._
  import org.http4s.client.blaze._

  import com.github.fd4s.http4s.aws._
  import com.github.fd4s.http4s.aws.model._

  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

  BlazeClientBuilder[IO](ec)
    .resource
    .map { client =>
      AwsSigner(
        CredentialsProvider.fromAwsCredentialProvider[IO](
          new DefaultAWSCredentialsProviderChain()
        ),
        Region.`eu-west-1`,
        Service.s3
      )(client)
    }
    .use { client =>
      for {
        url <- IO.fromEither(Uri.fromString("hhttps://test.s3-eu-west-1.amazonaws.com/test.txt"))
        req <- GET(url)
        res <- client.expect[String](req)
      } yield res
    }
    .unsafeRunSync()

````

Happy coding!
