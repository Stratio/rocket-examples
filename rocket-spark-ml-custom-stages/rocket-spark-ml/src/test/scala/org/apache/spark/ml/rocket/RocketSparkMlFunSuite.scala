/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package org.apache.spark.ml.rocket

import org.scalatest.{FunSuite, Outcome}


abstract class RocketSparkMlFunSuite extends FunSuite with RocketSparkMlBeforeAndAfterAll {

  /**
    * Log the suite name and the test name before and after each test.
    *
    * Subclasses should never override this method. If they wish to run
    * custom code before and after each test, they should mix in the
    * {{org.scalatest.BeforeAndAfter}} trait instead.
    */

  final protected override def withFixture(test: NoArgTest): Outcome = {
    val testName = test.text
    val suiteName = this.getClass.getName
    val shortSuiteName = suiteName.replaceAll("com.stratio.intelligence", "c.s.i")
    try {
      print(s"\n\n===== TEST OUTPUT FOR $shortSuiteName: '$testName' =====\n")
      test()

    } finally {
      print(s"\n\n===== FINISHED $shortSuiteName: '$testName' =====\n")
    }
  }
}
