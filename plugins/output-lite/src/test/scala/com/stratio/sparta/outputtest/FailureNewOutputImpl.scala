package com.stratio.sparta.outputtest

import com.stratio.sparta.sdk.lite.common.LiteCustomOutput
import org.apache.spark.sql.{DataFrame, SparkSession}

class FailureNewOutputImpl(
                            sparkSession: SparkSession,
                            properties: Map[String, String]
                          ) extends LiteCustomOutput(sparkSession, properties){

  override def save(data: DataFrame, saveMode: String, saveOptions: Map[String, String]): Unit =
    throw new RuntimeException("old method is being invoked")

}