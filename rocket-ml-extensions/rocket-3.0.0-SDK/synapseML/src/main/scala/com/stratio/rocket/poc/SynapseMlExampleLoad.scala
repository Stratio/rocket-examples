package com.stratio.rocket.poc

// Spark ML imports
import com.stratio.rocket.ml.serverclient.RocketMlClientBuilder
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.{StringIndexer, StringIndexerModel}
import org.apache.spark.sql.functions.rand

// SynapseML imports
import org.apache.spark.sql.SparkSession


object SynapseMlExampleLoad extends App {

  val sparkMasterIp = System.getProperty("spark.master", "local[2]")
  val spark = SparkSession
    .builder().master("local[2]")
    .appName("SynapseMlExample")
    .getOrCreate()
  val sc = spark.sparkContext


  // --------------------------------
  // => Load pipelineModel
  // --------------------------------

  val shapExplainerPipelineModel = PipelineModel.load("/tmp/shapExplainerModel")


  // --------------------------------
  // => Reading input dataset
  // --------------------------------

  val df = spark.read.parquet(
    "wasbs://publicwasb@mmlspark.blob.core.windows.net/AdultCensusIncome.parquet"
  ).cache()


  // ------------------------------------
  // => Target engineering
  // ------------------------------------

  val targetColName = "income"
  val labelIndexerModel: StringIndexerModel = new StringIndexer()
    .setInputCol(targetColName).setOutputCol("label").setStringOrderType("alphabetAsc").fit(df)
  val trainingDf = labelIndexerModel.transform(df)

  val featuresToPredict =  trainingDf.orderBy(rand()).limit(5).repartition(200)
  val explanationsDf = shapExplainerPipelineModel.transform(featuresToPredict)



  val rocketMlClient = new RocketMlClientBuilder()
    .withRocketURL("http://localhost:9090")
    .withSparkSession(spark)
    .build()

  rocketMlClient.mlModel.spark.saveModelAsNewAsset(
    pipelineModel = shapExplainerPipelineModel,
    assetPath = "synapse/fromlocal",
    trainDf = featuresToPredict,
    trainingTransformedDf = explanationsDf,
    evalDf = None
  )

}


