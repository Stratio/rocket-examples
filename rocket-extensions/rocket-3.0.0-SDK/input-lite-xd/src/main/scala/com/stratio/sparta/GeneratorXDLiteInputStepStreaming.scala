/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta

import org.apache.spark.sql._
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import com.stratio.sparta.sdk.lite.xd.streaming._
import com.stratio.sparta.sdk.lite.streaming.models._
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import org.apache.spark.sql.crossdata.XDSession

import scala.util.{Failure, Success, Try}
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types._
import org.apache.spark.streaming.StreamingContext

import scala.collection.mutable

class GeneratorXDLiteInputStepStreaming(
                                       xdSession: XDSession,
                                       streamingContext: StreamingContext,
                                       properties: Map[String, String]
                                     )
  extends LiteCustomXDStreamingInput(xdSession, streamingContext, properties) {

  lazy val stringSchema = StructType(Seq(StructField("raw", StringType)))
  lazy val rawData: Option[String] = properties.get("raw").map(_.toString)

  override def validate(): ValidationResult = {
    var validation = ValidationResult(valid = true, messages = Seq.empty, warnings = Seq.empty)

    if (rawData.isEmpty) {
      validation = ValidationResult(
        valid = false,
        warnings = validation.messages :+ "Test data must be set inside the Option properties with an option key named 'raw'")
    }

    if (rawData.map(_.trim).forall(_.isEmpty)) {
      validation = ValidationResult(
        valid = false,
        warnings = validation.messages :+ "Generated data cannot be an empty string")
    }
    validation
  }

  override def init(): ResultStreamingData = {
    val dataQueue = new mutable.Queue[RDD[Row]]()
    val register = Seq(new GenericRowWithSchema(Array(rawData.getOrElse("test-data")), stringSchema).asInstanceOf[Row])
    dataQueue += xdSession.sparkContext.parallelize(register)
    val stream = streamingContext.queueStream(dataQueue)

    // · Reporting messages
    reportInfoLog(phase="init", s"Generated data: $register")

    ResultStreamingData(stream, Option(stringSchema))
  }
}
