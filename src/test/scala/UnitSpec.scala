package com.github.fd4s.http4s.aws

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

abstract class UnitSpec
    extends AnyWordSpec
    with Matchers
    with ScalaCheckDrivenPropertyChecks
    with CatsEffectFutures {

  sys.props.put("log4j.configurationFile", "log4j2-ut.xml")

}
