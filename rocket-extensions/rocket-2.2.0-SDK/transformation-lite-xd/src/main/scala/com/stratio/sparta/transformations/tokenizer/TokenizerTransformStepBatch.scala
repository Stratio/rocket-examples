/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta.transformations.tokenizer

import java.io.{Serializable => JSerializable}

import com.stratio.sparta.properties.ValidatingPropertyMap._
import com.stratio.sparta.sdk.lite.batch.models._
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import com.stratio.sparta.sdk.lite.xd.batch._
import org.apache.spark.sql.Row
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.types._

import scala.util.Try

class TokenizerTransformStepBatch(
                        xdSession: XDSession,
                        properties: Map[String, String]
                                        ) extends LiteCustomXDBatchTransform(xdSession, properties) {

  lazy val splitByChar: Option[String] = Try(Option(properties.getString("charPattern"))).getOrElse(None).notBlank
  lazy val inputField : Option[String] = Try(Option(properties.getString("inputField"))).getOrElse(None).notBlank
  lazy val outputField1: Option[String] = Try(Option(properties.getString("outputField1"))).getOrElse(None).notBlank
  lazy val outputField2: Option[String] = Try(Option(properties.getString("outputField2"))).getOrElse(None).notBlank

  override def validate(): ValidationResult = {
    var validation = ValidationResult(valid = true, messages = Seq.empty)

    if (splitByChar.isEmpty) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "The char used to split the sentence must be defined with the option key 'charPattern'")
    }

    if (inputField.isEmpty) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "Indicate the input field name with the option key 'inputField'")
    }

    if (outputField1.isEmpty) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "Specify an option key 'outputField1' with the name of the first column")
    }

    if (outputField2.isEmpty) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "Specify an option key 'outputField2' with the name of the second column")
    }

    validation
  }

  override def transform(inputData: Map[String, ResultBatchData]): OutputBatchTransformData = {
    val inputStream = inputData.head._2.data
    val newFields = Seq(
      StructField(outputField1.get, StringType),
      StructField(outputField2.get, StringType)
    )
    val fieldToOperate = inputData.head._2.schema.get.fieldIndex(inputField.get)
    val splitByCharGet = splitByChar.get.charAt(0)
    val newSchema = Option(StructType(newFields))

    // · Reporting
    reportInfoLog("transform", s"Splitting column ${inputField.get} by char=$splitByCharGet to generate new schema: ${newSchema.get}")

    val result = inputStream.map{ row =>
      val splitValues = row.get(fieldToOperate).toString.split(splitByCharGet)

      Row.fromSeq(splitValues)
    }

    OutputBatchTransformData(result, newSchema)
  }
}
