/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.rocket.poc.plugins

import com.microsoft.azure.synapse.ml.explainers.TabularSHAP
import com.stratio.rocket.poc.plugins.properties.ValidatingPropertyMap._
import com.stratio.sparta.sdk.lite.common.models.OutputOptions
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import com.stratio.sparta.sdk.lite.xd.common.LiteCustomXDOutput
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.functions.{broadcast, rand}

import scala.util.Try


// TODO - Custom output

class ShapExplainerTrainerOutput(
                                  xdSession: XDSession,
                                  properties: Map[String, String]
                                ) extends LiteCustomXDOutput(xdSession, properties) {

  // ------------------------------
  // Input properties
  // ------------------------------

  // PipelineModel Path
  lazy val pipelineModelInputPath: Option[String] =
    Try(Option(properties.getString("pipelineModelInputPath"))).getOrElse(None).notBlank

  // Path where to save created Shap explainer
  lazy val shapExplainerOutputPath: Option[String] =
    Try(Option(properties.getString("shapExplainerOutputPath"))).getOrElse(None).notBlank

  // Input columns
  lazy val inputColumnsOpt: Option[String] =
    Try(Option(properties.getString("inputColumns"))).getOrElse(None).notBlank
  lazy val inputColumns: Array[String] = inputColumnsOpt.get.split(",")

  // Target column
  lazy val targetColumn: Option[String] =
    Try(Option(properties.getString("targetColumn"))).getOrElse(None).notBlank

  // Target classes
  lazy val targetClassesOpt: Option[String] =
    Try(Option(properties.getString("targetClasses"))).getOrElse(None).notBlank
  lazy val targetClasses: Array[Int] = targetClassesOpt.get.split(",").map(_.toInt)

  // Output column
  lazy val outputColumn: Option[String] =
    Try(Option(properties.getString("outputColumn"))).getOrElse(None).notBlank

  // Num. samples for background data
  lazy val numSamplesBackgroundDataOpt: Option[String] =
    Try(Option(properties.getString("numSamplesBackgroundData"))).getOrElse(None).notBlank
  lazy val numSamplesBackgroundData: Int = numSamplesBackgroundDataOpt.get.toInt

  // Num. samples for internal generator
  lazy val numSamplesGeneratorOpt: Option[String] =
    Try(Option(properties.getString("numSamplesGenerator"))).getOrElse(None).notBlank
  lazy val numSamplesGenerator: Int = numSamplesGeneratorOpt.get.toInt

  lazy val mandatoryProperties: Seq[(Option[String], String)] = Seq(
    pipelineModelInputPath -> "pipelineModelInputPath",
    shapExplainerOutputPath -> "shapExplainerOutputPath",
    inputColumnsOpt -> "inputColumns",
    targetColumn -> "targetColumn",
    targetClassesOpt -> "targetClasses",
    outputColumn -> "outputColumn",
    numSamplesBackgroundDataOpt -> "numSamplesBackgroundData",
    numSamplesGeneratorOpt -> "numSamplesGenerator"
  )

  // ------------------------------
  // => Validating properties
  // ------------------------------

  private def validateNonEmpty(property: Option[_], propertyName: String, validationResult: ValidationResult): (ValidationResult, Boolean) = {
    if (property.isEmpty) {
      ValidationResult(
        valid = false, messages = validationResult.messages :+ s"'$propertyName' is empty'"
      ) -> false
    } else {
      validationResult -> true
    }
  }

  override def validate(): ValidationResult = {
    var validation = ValidationResult(valid = true, messages = Seq.empty, warnings = Seq.empty)

    for((prop, propName) <- mandatoryProperties){
      val (propValidation, _ ) = validateNonEmpty(prop, propName, validation)
      validation = propValidation
    }

    validation
  }


  // ------------------------------
  // => Save logic
  // ------------------------------

  override def save(data: DataFrame, outputOptions: OutputOptions): Unit = {
    val trainingDf = data

    // => Load SparkML pipelineModel
    val pipelineModel = PipelineModel.load(pipelineModelInputPath.get)

    // => Get background data as DataFrame
    val backgroudDataDf = broadcast(trainingDf.orderBy(rand()).limit(this.numSamplesBackgroundData).cache())

    // => Create shapExplainer
    val shapExplainer = new TabularSHAP()
      .setInputCols(inputColumns)
      .setOutputCol(outputColumn.get)
      .setNumSamples(numSamplesGenerator)
      .setModel(pipelineModel)
      .setTargetCol(targetColumn.get)
      .setTargetClasses(targetClasses)
      .setBackgroundData(backgroudDataDf)

    // => Save shapExplainer
    shapExplainer.save(shapExplainerOutputPath.get)
  }

  override def save(data: DataFrame, saveMode: String, saveOptions: Map[String, String]): Unit = ()
}