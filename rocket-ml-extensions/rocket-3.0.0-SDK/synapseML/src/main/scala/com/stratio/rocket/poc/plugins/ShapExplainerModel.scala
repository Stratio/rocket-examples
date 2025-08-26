/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.rocket.poc.plugins

import com.microsoft.azure.synapse.ml.explainers.TabularSHAP
import com.stratio.rocket.poc.plugins.properties.ValidatingPropertyMap._
import com.stratio.sparta.sdk.lite.batch.models.{OutputBatchTransformData, ResultBatchData}
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import com.stratio.sparta.sdk.lite.xd.batch.LiteCustomXDBatchTransform
import org.apache.spark.ml.Transformer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.types.StructType

import scala.util.Try

// com.stratio.rocket.poc.plugins.ShapExplainerModel

class ShapExplainerModel(
                          xdSession: XDSession,
                          properties: Map[String, String]
                        ) extends LiteCustomXDBatchTransform(xdSession, properties) {

  // ------------------------------
  // Input properties
  // ------------------------------

  lazy val shapExplainerPath: Option[String] =
    Try(Option(properties.getString("shapExplainerPath"))).getOrElse(None).notBlank


  // ------------------------------
  // => Validating properties
  // ------------------------------

  override def validate(): ValidationResult = {
    var validation = ValidationResult(valid = true, messages = Seq.empty, warnings = Seq.empty)

    if (shapExplainerPath.isEmpty) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "'shapExplainerPath' is empty'")
    }
    validation
  }


  // ------------------------------
  // => Transform logic
  // ------------------------------

  override def transform(inputData: Map[String, ResultBatchData]): OutputBatchTransformData = {

    // => Load TabularSHAP model
    val shapExplainerModel = TabularSHAP.load(shapExplainerPath.get)

    // => Getting MLModel == Spark PipelineModel
    val pipelineModel: Transformer = shapExplainerModel.getModel

    // => Getting input data
    val inputRDD: RDD[Row] = inputData.head._2.data
    val inputSchema = inputData.head._2.schema.getOrElse(new StructType())
    val inputDf = xdSession.createDataFrame(inputRDD, inputSchema)

    // => Inference: making predictions
    val predictionDf = pipelineModel.transform(inputDf)

    // => Explainability: explain predictions
    val explanationsDF = shapExplainerModel.transform(predictionDf)

    // => Output data
    OutputBatchTransformData(explanationsDF.rdd, Option(explanationsDF.schema))
  }
}

