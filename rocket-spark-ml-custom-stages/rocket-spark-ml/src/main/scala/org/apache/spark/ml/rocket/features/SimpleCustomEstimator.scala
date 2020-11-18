/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package org.apache.spark.ml.rocket.features

import org.apache.hadoop.fs.Path
import org.apache.spark.ml.param.{Param, ParamMap, Params}
import org.apache.spark.ml.util._
import org.apache.spark.ml.{Estimator, Model}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset}

/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */


// ------------------------------------------------------------------------------------------------
//  Shared parameters between Estimator and Transformer
// ------------------------------------------------------------------------------------------------

trait SimpleIndexerParams extends Params {
  final val inputCol = new Param[String](this, "inputCol", "The input column")
  final val outputCol = new Param[String](this, "outputCol", "The output column")
}


// ------------------------------------------------------------------------------------------------
//  Estimator
// ------------------------------------------------------------------------------------------------

class SimpleIndexer(override val uid: String)
  extends Estimator[SimpleIndexerModel] with SimpleIndexerParams with DefaultParamsWritable {

  def setInputCol(value: String): this.type  = set(inputCol, value)

  def setOutputCol(value: String): this.type  = set(outputCol, value)

  def this() = this(Identifiable.randomUID("simpleindexer"))

  override def copy(extra: ParamMap): SimpleIndexer = {
    defaultCopy(extra)
  }

  override def transformSchema(schema: StructType): StructType = {
    // Check that the input type is a string
    val idx = schema.fieldIndex($(inputCol))
    val field = schema.fields(idx)
    if (field.dataType != StringType) {
      throw new Exception(s"Input type ${field.dataType} did not match input type StringType")
    }
    // Add the return field
    schema.add(StructField($(outputCol), IntegerType, false))
  }

  override def fit(dataset: Dataset[_]): SimpleIndexerModel = {
    import dataset.sparkSession.implicits._
    val words = dataset.select(dataset($(inputCol)).as[String]).distinct.collect()

    copyValues(new SimpleIndexerModel(uid, words)).setParent(this)
  }

}

object SimpleIndexer extends DefaultParamsReadable[SimpleIndexer]{
  override def load(path: String): SimpleIndexer = super.load(path)
}
// ------------------------------------------------------------------------------------------------
//  Transformer
// ------------------------------------------------------------------------------------------------

class SimpleIndexerModel(
                          override val uid: String,
                          words: Array[String]
                        ) extends Model[SimpleIndexerModel] with SimpleIndexerParams with MLWritable{

  import SimpleIndexerModel._

  override def copy(extra: ParamMap): SimpleIndexerModel = {
    defaultCopy(extra)
  }

  private def getWords: Array[String] = words

  private val labelToIndex: Map[String, Double] = words.zipWithIndex.
    map { case (x, y) => (x, y.toDouble) }.toMap

  override def transformSchema(schema: StructType): StructType = {
    // Check that the input type is a string
    val idx = schema.fieldIndex($(inputCol))
    val field = schema.fields(idx)
    if (field.dataType != StringType) {
      throw new Exception(s"Input type ${field.dataType} did not match input type StringType")
    }
    // Add the return field
    schema.add(StructField($(outputCol), IntegerType, false))
  }

  override def transform(dataset: Dataset[_]): DataFrame = {
    val indexer = udf { label: String => labelToIndex(label) }
    dataset.select(col("*"),
      indexer(dataset($(inputCol)).cast(StringType)).as($(outputCol)))
  }

  override def write: MLWriter = new SimpleIndexerModelWriter(this)
}

object SimpleIndexerModel extends MLReadable[SimpleIndexerModel] {

  // *********************
  //  Writer
  // *********************

  private[SimpleIndexerModel] class SimpleIndexerModelWriter(instance: SimpleIndexerModel) extends MLWriter {

    private case class Data(words: Seq[String])

    override protected def saveImpl(path: String): Unit = {
      // · Saving metadata
      DefaultParamsWriter.saveMetadata(instance, path, sc)
      // · Saving data
      val data = Data(words = instance.getWords)
      val dataPath = new Path(path, "data").toString
      sparkSession.createDataFrame(Seq(data)).repartition(1).write.parquet(dataPath)
    }
  }


  // *********************
  //  Reader
  // *********************

  private class SimpleIndexerModelReader extends MLReader[SimpleIndexerModel] {

    private val className = classOf[SimpleIndexerModel].getName

    override def load(path: String): SimpleIndexerModel = {
      // · Reading metadata
      val metadata = DefaultParamsReader.loadMetadata(path, sc, className)
      // · Reading data
      val dataPath = new Path(path, "data").toString
      val data = sparkSession.read.parquet(dataPath)
        .select("words")
        .head()
      val words = data.getAs[Seq[String]](0).toArray
      // · Construct SimpleIndexerModel
      val model = new SimpleIndexerModel(metadata.uid, words)
      metadata.getAndSetParams(model)
      model
    }
  }

  // => Ml Readable inteface
  override def read:MLReader[SimpleIndexerModel] = new SimpleIndexerModelReader

  override def load(path: String): SimpleIndexerModel = super.load(path)
}