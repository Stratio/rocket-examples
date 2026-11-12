/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta.transformations.column

import com.stratio.sparta.sdk.lite.batch.models._
import com.stratio.sparta.sdk.lite.xd.batch._
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.types.StructType

class AddColumnXDLiteTransformStepBatch(
                                         xdSession: XDSession,
                                         properties: Map[String, String]
                                       ) extends LiteCustomXDBatchTransform(xdSession, properties) {

  override def transform(inputData: Map[String, ResultBatchData]): OutputBatchTransformData = {
    // Get input data and schema
    val inputStream = inputData.head._2.data
    val inputSchema = inputData.head._2.schema.getOrElse(new StructType())

    // Convert to DataFrame and make modifications
    val df = xdSession.createDataFrame(inputStream, inputSchema)
    val dfWithColumn = df.withColumn("newCol", lit(2))

    // Return the transformed data
    OutputBatchTransformData(dfWithColumn.rdd, Option(dfWithColumn.schema))
  }
}
