package com.stratio.sparta.transformations.tokenizer

import com.stratio.sparta.sdk.lite.batch.models.ResultBatchData
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TokenizerTransformStepBatchTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  val sc = SparkContext.getOrCreate(new SparkConf().setAppName("test").setMaster("local[1]"))
  val xdSession: XDSession = XDSession.builder().config(sc.getConf).create("dummyUser")

  val names = "jose,perez"
  val inputField = "raw"
  val inputSchema = StructType(Seq(StructField(inputField, StringType)))
  val dataIn: RDD[Row] = sc.parallelize(Seq(Row(names)))

  val properties = Map(
    "charPattern" -> ",",
    "inputField" -> "raw",
    "outputField1" -> "firstName",
    "outputField2" -> "lastName"
  )

  val inBatch = ResultBatchData(dataIn, Option(inputSchema))
  val tokenizer = new TokenizerTransformStepBatch(xdSession, properties)

  "TokenizerTransformStepBatch" should "tokenize the data in and return two values" in {
    val result = tokenizer.transform(Map("custom-transform" -> inBatch)).data.first().toSeq

    result.size shouldBe 2
  }
}