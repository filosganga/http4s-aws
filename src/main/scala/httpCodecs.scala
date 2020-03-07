package com.github.fd4s.http4s.aws

import model.Credentials._

import java.time._
import java.time.format.DateTimeFormatter
import scala.util.Try
import cats.implicits._

import org.http4s._
import org.http4s.util.Writer

object httpCodecs {

  implicit val httpDateHttpCodec: HttpCodec[HttpDate] =
    new HttpCodec[HttpDate] {

      private val dateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

      override def parse(s: String): ParseResult[HttpDate] =
        Try(ZonedDateTime.parse(s, dateTimeFormatter)).toEither
          .leftMap(_ =>
            ParseFailure(
              "Error to parse a datetime",
              s"The string `$s` is not a valid datetime"
            )
          )
          .flatMap(a => HttpDate.fromEpochSecond(a.toEpochSecond))

      override def render(writer: Writer, t: HttpDate): writer.type = {
        val formattedDateTime =
          Instant
            .ofEpochSecond(t.epochSecond)
            .atOffset(ZoneOffset.UTC)
            .format(dateTimeFormatter)
        writer << formattedDateTime
      }
    }

  implicit val sessionTokenHttpCodec: HttpCodec[SessionToken] =
    new HttpCodec[SessionToken] {

      override def parse(s: String): ParseResult[SessionToken] =
        SessionToken(s).asRight

      override def render(writer: Writer, t: SessionToken): writer.type =
        writer << t.value
    }

}