package com.stratio.sparta.outputtest

import com.stratio.sparta.sdk.lite.common.LiteCustomOutput
import com.stratio.sparta.sdk.lite.common.models.OutputOptions
import org.apache.spark.sql.{DataFrame, SparkSession}


class SuccessNewOutputImpl(
                  sparkSession: SparkSession,
                  properties: Map[String, String]
                ) extends LiteCustomOutput(sparkSession, properties){

  override def save(data: DataFrame, saveMode: String, saveOptions: Map[String, String]): Unit =
    throw new Exception("old method should not be invoked")

  override def save(data: DataFrame, outputOptions: OutputOptions): Unit =
    throw new RuntimeException("new method is being invoked")
}
