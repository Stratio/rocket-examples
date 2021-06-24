package com.stratio.sparta.transformations.tokenizer

import com.stratio.sparta.properties.ValidatingPropertyMap._
import com.stratio.sparta.sdk.lite.streaming.models.{OutputStreamingTransformData, ResultStreamingData}
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import com.stratio.sparta.sdk.lite.xd.streaming.LiteCustomXDStreamingTransform
import org.apache.spark.sql.Row
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.streaming.StreamingContext

import scala.util.Try

class TokenizerTransformStepStreaming(
                                       xdSession: XDSession,
                                       streamingContext: StreamingContext,
                                       properties: Map[String, String]
                                     ) extends LiteCustomXDStreamingTransform(xdSession, streamingContext, properties) {

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

  override def transform(inputData: Map[String, ResultStreamingData]): OutputStreamingTransformData = {
    val inputStream = inputData.head._2.data
    val newFields = Seq(
      StructField(outputField1.get, StringType),
      StructField(outputField2.get, StringType)
    )
    val splitByCharGet = splitByChar.get.charAt(0)
    val newSchema = Option(StructType(newFields))

    val result = inputStream.map{ row =>
      val splitValues = row.getAs[String](inputField.get).split(splitByCharGet)

      Row.fromSeq(splitValues)
    }

    OutputStreamingTransformData(result, newSchema)
  }
}
