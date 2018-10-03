/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta

import com.stratio.sparta.core.enumerators.SaveModeEnum
import com.stratio.sparta.plugin.workflow.output.custom.CustomLiteOutputStep
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.crossdata.XDSession
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class CustomLiteOutputStepTests
  extends FlatSpec with Matchers with BeforeAndAfterAll {

  val sc = SparkContext.getOrCreate(new SparkConf().setAppName("test").setMaster("local[1]"))
  val xdSession: XDSession = XDSession.builder().config(sc.getConf).create("dummyUser")

  private val data = {
    import xdSession.implicits._
    sc.parallelize(Seq(Person("Marcos", 18), Person("Juan", 21), Person("Jose", 26))).toDS().toDF
  }


  "CustomLiteOutput" should "invoke only the new method when has been implemented" in {
    val properties = Map("customLiteClassType" -> "com.stratio.sparta.outputtest.SuccessNewOutputImpl")
    val customLiteOutput = new CustomLiteOutputStep("customlite-test", xdSession, properties)

    the [RuntimeException] thrownBy {
      customLiteOutput.save(data, SaveModeEnum.ErrorIfExists, Map.empty)
    } should have message "new method is being invoked"

  }

  it should "keep working with legacy LiteOutputStep" in {
    val properties = Map("customLiteClassType" -> "com.stratio.sparta.outputtest.FailureNewOutputImpl")
    val customLiteOutput = new CustomLiteOutputStep("customlite-test", xdSession, properties)

    the [RuntimeException] thrownBy {
      customLiteOutput.save(data, SaveModeEnum.ErrorIfExists, Map.empty)
    } should have message "old method is being invoked"
  }

}

case class Person(name: String, age: Int) extends Serializable