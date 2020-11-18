from pyspark.ml import Pipeline
from pyspark.sql import SparkSession

from rocket_pyspark_ml.simple_custom_estimator import NormalDeviation

spark = SparkSession.builder \
    .master("local") \
    .appName("test") \
    .getOrCreate()

df = spark.sparkContext.parallelize([(1, 2.0), (2, 3.0), (3, 0.0), (4, 99.0)]).toDF(["id", "x"])

normal_deviation = NormalDeviation().setInputCol("x").setCenteredThreshold(1.0)

pipeline = Pipeline(stages=[normal_deviation])

model = pipeline.fit(df)

out_df = model.transform(df)
out_df.show()

pipeline.write().overwrite().save("/tmp/my_custom_pipeline")
model.write().overwrite().save("/tmp/my_custom_model_from_custom_pipeline")
