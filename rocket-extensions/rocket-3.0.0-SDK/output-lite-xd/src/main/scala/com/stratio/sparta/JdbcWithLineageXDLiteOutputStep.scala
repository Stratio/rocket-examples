/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta

import com.stratio.sparta.sdk.lite.common.models.OutputOptions
import com.stratio.sparta.sdk.lite.xd.common._
import org.apache.spark.sql._
import org.apache.spark.sql.crossdata.XDSession

import java.util.Properties
import scala.util.{Failure, Success, Try}

class JdbcWithLineageXDLiteOutputStep(
                              xdSession: XDSession,
                              properties: Map[String, String]
                            )
  extends LiteCustomXDOutput(xdSession, properties) {

  lazy val url = properties.getOrElse("url", throw new NoSuchElementException("The url property is mandatory"))

  // Lineage options, usually extracted from 'url' or other properties as 'dbtable'
  // TODO - SDK en master no parece tener alguno de estos métodos para sobre-escribir
/*  override def lineageService(): Option[String] = properties.get("service")
  override def lineagePath(): Option[String] = properties.get("path")
  override def lineageResource(): Option[String] = properties.get("resource") // If empty will be populated by the system the writer tableName
  override def lineageDatastoreType(): Option[String] = properties.get("datastoreType")*/

  override def save(data: DataFrame, outputOptions: OutputOptions): Unit = {
    val tableName = outputOptions.tableName.getOrElse{
      logger.error("Table name not defined")
      throw new NoSuchElementException("tableName not found in options")
    }

    val jdbcProperties = new Properties()

    properties
      .filterKeys(key => key.startsWith("jdbc_") || key.equals("driver"))
      .foreach{ case (key, value) => jdbcProperties.put(key.replaceAll("jdbc_", ""), value) }

    logger.error(s"Connecting with table $tableName")
    logger.error(s"Connecting with properties $jdbcProperties")

    data.write
     .mode(SaveMode.Append)
     .jdbc(url = url, table = tableName, connectionProperties = jdbcProperties)
  }

  override def save(data: DataFrame, saveMode: String, saveOptions: Map[String, String]): Unit = ()
}
