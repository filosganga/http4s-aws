/*
 * Copyright 2019-2020 fd4s
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.fd4s.http4s.aws

import cats.effect._
import cats.implicits._

import com.amazonaws.auth.{
  AWSSessionCredentials,
  AWSCredentialsProvider,
  DefaultAWSCredentialsProviderChain
}

import model._
import Credentials._

trait CredentialsProvider[F[_]] {
  def get: F[Credentials]
}

object CredentialsProvider {

  def default[F[_]: Sync]: CredentialsProvider[F] =
    fromAwsCredentialProvider[F](new DefaultAWSCredentialsProviderChain)

  // TODO refresh creds automagically
  def fromAwsCredentialProvider[F[_]](
      awsCredentialsProvider: AWSCredentialsProvider
  )(implicit F: Sync[F]): CredentialsProvider[F] =
    new CredentialsProvider[F] {
      override def get: F[Credentials] =
        F.delay(awsCredentialsProvider.getCredentials).map {
          case creds: AWSSessionCredentials =>
            Credentials(
              AccessKeyId(creds.getAWSAccessKeyId),
              SecretAccessKey(creds.getAWSSecretKey),
              SessionToken(creds.getSessionToken).some
            )
          case creds =>
            Credentials(
              AccessKeyId(creds.getAWSAccessKeyId),
              SecretAccessKey(creds.getAWSSecretKey),
              None
            )
        }
    }

  def resolveFromEnvironmentVariables: Option[Credentials] = {

    val accessKeyIdEnv = "AWS_ACCESS_KEY_ID"
    val secretAccessKey = "AWS_SECRET_ACCESS_KEY"
    val sessionTokenEnv = "AWS_SESSION_TOKEN"

    // These are legacy
    val accessKey = "AWS_ACCESS_KEY"
    val secretKeyEnv = "AWS_SECRET_KEY"

    (
      sys.env
        .get(accessKeyIdEnv)
        .orElse(sys.env.get(accessKey))
        .map(AccessKeyId.apply),
      sys.env
        .get(secretKeyEnv)
        .orElse(sys.env.get(secretAccessKey))
        .map(SecretAccessKey.apply)
    ).mapN { (accessKeyId, secretAccessKey) =>
      val sessionToken = sys.env
        .get(sessionTokenEnv)
        .map(SessionToken.apply)
      Credentials(accessKeyId, secretAccessKey, sessionToken)
    }
  }

  def resolveFromSystemProperties: Option[Credentials] = {

    val accessKeyIdProperty = "aws.accessKeyId"
    val secretKeyProperty = "aws.secretKey"
    val sessionTokenProperty = "aws.sessionToken"

    (
      sys.props.get(accessKeyIdProperty).map(AccessKeyId.apply),
      sys.props.get(secretKeyProperty).map(SecretAccessKey.apply)
    ).mapN { (accessKeyId, secretAccessKey) =>
      val sessionToken = sys.props
        .get(sessionTokenProperty)
        .map(SessionToken.apply)

      Credentials(accessKeyId, secretAccessKey, sessionToken)
    }
  }

}