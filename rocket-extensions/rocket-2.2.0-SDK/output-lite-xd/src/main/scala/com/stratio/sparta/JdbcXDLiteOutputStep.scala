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

class JdbcXDLiteOutputStep(
                              xdSession: XDSession,
                              properties: Map[String, String]
                            )
  extends LiteCustomXDOutput(xdSession, properties) {

  lazy val url = properties.getOrElse("url", throw new NoSuchElementException("The url is mandatory"))

  // Lineage options, usually extracted from 'url' or other properties as 'dbtable'
  lazy val service = properties.get("service")
  lazy val path = properties.get("path")
  lazy val resource = properties.get("resource")
  lazy val dataType = properties.get("dataType")


  override def save(data: DataFrame, outputOptions: OutputOptions): Unit = {
    val tableName = outputOptions.tableName.getOrElse{
      logger.error("Table name not defined")
      throw new NoSuchElementException("tableName not found in options")}

    data.write.jdbc(url = url, table = tableName, connectionProperties = new Properties())
  }

  override def save(data: DataFrame, saveMode: String, saveOptions: Map[String, String]): Unit = ()
}
