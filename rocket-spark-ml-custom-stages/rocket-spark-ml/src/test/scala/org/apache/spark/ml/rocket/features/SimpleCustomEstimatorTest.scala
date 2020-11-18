/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package org.apache.spark.ml.rocket.features

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.rocket.RocketSparkMlFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SimpleCustomEstimatorTest extends RocketSparkMlFunSuite{

  test("Example"){

    // => Prepare training documents from a list of (id, text, label) tuples.
    val df = spark.createDataFrame(Seq(
      (0L, "a b c d e spark", 1.0, "cat0"),
      (1L, "b d", 0.0, "cat0"),
      (2L, "spark f g h", 1.0, "cat1"),
      (3L, "hadoop mapreduce", 0.0, "cat2")
    )).toDF("id", "text", "label", "cat")

    // · Feature engineering
    val si = new SimpleIndexer().setInputCol("cat").setOutputCol("idx_cat")
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val hashingTF = new HashingTF().setNumFeatures(1000).setInputCol(tokenizer.getOutputCol).setOutputCol("features")
    val lr = new LogisticRegression().setMaxIter(10).setRegParam(0.001)
    val pipeline = new Pipeline().setStages(Array(tokenizer, hashingTF, si, lr))


    val pipelineModel = pipeline.fit(df)

    val outDf = pipelineModel.transform(df)
    outDf.show()

    pipeline.write.overwrite().save("/tmp/custom_pipeline")
    val newPipeline = Pipeline.load("/tmp/custom_pipeline")

    pipelineModel.write.overwrite().save("/tmp/custom_pipeline_model")
    val newPipelineModel = PipelineModel.load("/tmp/custom_pipeline_model")
  }
}
