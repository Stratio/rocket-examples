/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta

import com.stratio.sparta.sdk.lite.hybrid.models.ResultHybridData
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import com.stratio.sparta.sdk.lite.xd.hybrid._
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.types._

class StreamGeneratorXDLiteInputStepHybrid(
                                   xdSession: XDSession,
                                   properties: Map[String, String]
                                 )
  extends LiteCustomXDHybridInput(xdSession, properties) {

  lazy val rowsPerSecond: Option[String] = properties.get("rowsPerSecond").map(_.toString)

  override def init(): ResultHybridData = {
    val dataFrame: Dataset[Row] = xDSession.readStream
      .format("rate")
      .option("rowsPerSecond", rowsPerSecond.getOrElse("1"))
      .load()

    ResultHybridData(dataFrame)
  }

  // This method is used in order to provide an equivalent Batch Dataframe for debugging purposes
  def debugInit(): Option[DataFrame] = {
    import xdSession.implicits._

    Option(Seq(
      (8, "Lazarillo de Tormes"),
      (64, "Codex Seraphinianus"),
      (27, "Divina Commedia")
    ).toDF("price", "book"))
  }
}
