/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta

import com.stratio.sparta.sdk.lite.streaming._
import com.stratio.sparta.sdk.lite.streaming.models._
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types._
import org.apache.spark.streaming.StreamingContext

import scala.collection.mutable

class GeneratorLiteInputStepStreaming(
                                       sparkSession: SparkSession,
                                       streamingContext: StreamingContext,
                                       properties: Map[String, String]
                                     )
  extends LiteCustomStreamingInput(sparkSession, streamingContext, properties) {

  lazy val stringSchema = StructType(Seq(StructField("raw", StringType)))
  lazy val rawData: Option[String] = properties.get("raw").map(_.toString)


  override def validate(): ValidationResult = {
    var validation = ValidationResult(valid = true, messages = Seq.empty)

    if (rawData.isEmpty) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "Test data must be set inside the Option properties with an option key named 'raw'")
    }

    if (rawData.map(_.trim).forall(_.isEmpty)) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "Generated data cannot be an empty string")
    }
    validation
  }

  override def init(): ResultStreamingData = {
    val dataQueue = new mutable.Queue[RDD[Row]]()
    val register = Seq(new GenericRowWithSchema(Array(rawData.get), stringSchema).asInstanceOf[Row])
    dataQueue += sparkSession.sparkContext.parallelize(register)
    val stream = streamingContext.queueStream(dataQueue)

    ResultStreamingData(stream, Option(stringSchema))
  }
}
