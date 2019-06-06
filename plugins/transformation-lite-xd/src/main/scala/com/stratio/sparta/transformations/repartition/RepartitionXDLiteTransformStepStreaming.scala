/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta.transformations.repartition

import com.stratio.sparta.sdk.lite.streaming.models._
import com.stratio.sparta.sdk.lite.xd.streaming._
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.streaming.StreamingContext

class RepartitionXDLiteTransformStepStreaming(
                                             xdSession: XDSession,
                                             streamingContext: StreamingContext,
                                             properties: Map[String, String]
                                           ) extends LiteCustomXDStreamingTransform(xdSession, streamingContext, properties) {

  override def transform(inputData: Map[String, ResultStreamingData]): OutputStreamingTransformData = {
    val newStream = inputData.head._2.data.transform { rdd =>
      rdd.repartition(5)
    }

    OutputStreamingTransformData(newStream)
  }
}
