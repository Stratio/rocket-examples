package com.stratio.rocket.poc

// Spark ML imports
import com.stratio.rocket.ml.serverclient.RocketMlClientBuilder
import org.apache.spark.ml.classification._
import org.apache.spark.ml.feature._
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.functions._

// SynapseML imports
import com.microsoft.azure.synapse.ml.explainers._
import org.apache.spark.sql.SparkSession


object SynapseMlExample extends App {

  val sparkMasterIp = System.getProperty("spark.master", "local[2]")
  val spark = SparkSession
    .builder().master("local[2]")
    .appName("SynapseMlExample")
    .getOrCreate()
  val sc = spark.sparkContext


  // --------------------------------
  // => Reading input dataset
  // --------------------------------

  val df = spark.read.parquet(
    "wasbs://publicwasb@mmlspark.blob.core.windows.net/AdultCensusIncome.parquet"
  ).cache()


  // ------------------------------------
  // => Target engineering
  // ------------------------------------

  // => Indexing target column
  val targetColName = "income"
  val labelIndexerModel: StringIndexerModel = new StringIndexer()
    .setInputCol(targetColName).setOutputCol("label").setStringOrderType("alphabetAsc").fit(df)

  val trainingDf = labelIndexerModel.transform(df)


  val featuresToPredictcsv =  trainingDf.orderBy(rand()).limit(5).repartition(200)

  /*
  // Saving to CSV; note, some data contains trailing whitespaces

  featuresToPredictcsv.coalesce(1).write
    .option("header", true)
    .option("ignoreLeadingWhiteSpace", false)
    .option("ignoreTrailingWhiteSpace", false)
    .csv("/tmp/AdultCensusIncome_5samples.csv")
  */


  // => Training process setup
  // ------------------------------------

  // => Feature engineering
  // ************************

  val categoricalFeatures = Array(
    "workclass", "education", "marital-status", "occupation", "relationship", "race", "sex", "native-country"
  )

  val numericFeatures = Array("age", "education-num", "capital-gain", "capital-loss", "hours-per-week")

  val weightCol = "fnlwgt"

  // - Pre-processing categorical features: StringIndexer + OneHotEncoder
  val categorical_features_idx = categoricalFeatures.map(col => s"${col}_idx")
  val strIndexer = new StringIndexer()
    .setInputCols(categoricalFeatures)
    .setOutputCols(categorical_features_idx)

  val categorical_features_enc = categoricalFeatures.map(col => s"${col}_enc")
  val onehotEnc = new OneHotEncoder()
    .setInputCols(categorical_features_idx)
    .setOutputCols(categorical_features_enc)

  // - Assembling all features in one vector
  val vectAssem = new VectorAssembler()
    .setInputCols(categorical_features_enc ++ numericFeatures)
    .setOutputCol("features")


  // => Trainer definition
  // ************************
  val lr = new LogisticRegression()
    .setFeaturesCol("features")
    .setLabelCol("label")
    .setWeightCol(weightCol)

  // => Complete pipeline
  // ************************
  val pipeline = new Pipeline()
    .setStages(Array(strIndexer, onehotEnc, vectAssem, lr))


  // ------------------------------------
  // => Executing training process
  // ------------------------------------

  val model = pipeline.fit(trainingDf)


  // ------------------------------------
  // => Executing inference process
  // ------------------------------------

  val predictions = model.transform(trainingDf).cache()


  // ------------------------------------
  // => Explainability
  // ------------------------------------

  val featuresToPredict =  trainingDf.orderBy(rand()).limit(5).repartition(200)

  // => Inference
  val predictionsToExplain = model.transform(featuresToPredict)


  // => Explainer setup
  // ************************

  val shapExplainer = new TabularSHAP()
    .setInputCols(categoricalFeatures ++ numericFeatures)
    .setOutputCol("shapValues")
    .setNumSamples(5000)
    .setModel(model)
    .setTargetCol("probability")
    .setTargetClasses(Array(1))
    .setBackgroundData(broadcast(trainingDf.orderBy(rand())).limit(100).cache())

  // · Explain predictions
  val explanationsDf = shapExplainer.transform(predictionsToExplain)
  explanationsDf.show()

  // · Predict and explain from features
  val explanationsFromFeaturesDf = shapExplainer.transform(featuresToPredict)
  explanationsFromFeaturesDf.show()


  val shapExplainerPipelineModel = new Pipeline().setStages(Array(shapExplainer)).fit(trainingDf)
  shapExplainerPipelineModel.save("/tmp/shapExplainerModel")


}


