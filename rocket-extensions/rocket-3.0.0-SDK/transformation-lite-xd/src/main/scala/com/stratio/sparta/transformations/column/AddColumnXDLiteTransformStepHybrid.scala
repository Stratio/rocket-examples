/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta.transformations.column

import com.stratio.sparta.sdk.lite.hybrid.models.{OutputHybridTransformData, ResultHybridData}
import com.stratio.sparta.sdk.lite.xd.hybrid.LiteCustomXDHybridTransform
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.functions.lit

class AddColumnXDLiteTransformStepHybrid(
                                          xdSession: XDSession,
                                          properties: Map[String, String]
                                        ) extends LiteCustomXDHybridTransform(xdSession, properties) {

  override def transform(inputData: Map[String, ResultHybridData]): OutputHybridTransformData = {
    // Get input data and schema
    val inputStream = inputData.head._2.data

    val dfWithColumn = inputStream.withColumn("newCol", lit(2))

    // Return the transformed data
    OutputHybridTransformData(dfWithColumn)
  }
}
