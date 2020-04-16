/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta

import com.stratio.sparta.sdk.lite.common._
import com.stratio.sparta.sdk.lite.common.models.OutputOptions
import org.apache.spark.sql._

import scala.util.{Failure, Success, Try}

class LoggerLiteOutputStep(
                              sparkSession: SparkSession,
                              properties: Map[String, String]
                            )
  extends LiteCustomOutput(sparkSession, properties) {

  lazy val metadataEnabled = properties.get("metadataEnabled") match {
    case Some(value: String) => Try(value.toBoolean) match {
      case Success(v) => v
      case Failure(ex) => throw new IllegalStateException(s"$value not parseable to boolean", ex)
    }
    case None => throw new IllegalStateException("'metadataEnabled' key must be defined in the Option properties")
  }

  override def save(data: DataFrame, outputOptions: OutputOptions): Unit = {
    val tableName = outputOptions.tableName.getOrElse{
      logger.error("Table name not defined")
      throw new NoSuchElementException("tableName not found in options")}

    if (metadataEnabled){
      logger.info(s"Table name: $tableName")
      logger.info(s"Save mode is set to ${outputOptions.saveMode}")
    }
    data.foreach{ row =>
      println(row.mkString(","))
    }
  }

  override def save(data: DataFrame, saveMode: String, saveOptions: Map[String, String]): Unit = ()

}
