from pyspark.ml import Pipeline, PipelineModel
from pyspark.ml.classification import LogisticRegression
from pyspark.ml.feature import HashingTF, Tokenizer
from pyspark.sql import SparkSession

from rocket_pyspark_ml.simple_custom_transformer import LiteralColumnAdder

spark = SparkSession.builder \
    .master("local") \
    .appName("test") \
    .getOrCreate()

# Prepare training documents from a list of (id, text, label) tuples.
df = spark.createDataFrame([
    (0, "a b c d e spark", 1.0),
    (1, "b d", 0.0),
    (2, "spark f g h", 1.0),
    (3, "hadoop mapreduce", 3.0)
], ["id", "text", "label"])

# Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
tokenizer = Tokenizer(inputCol="text", outputCol="words")
hashingTF = HashingTF(inputCol=tokenizer.getOutputCol(), outputCol="features", numFeatures=1000)
lr = LogisticRegression(maxIter=10, regParam=0.001)

# Custom transformer
custom = LiteralColumnAdder()


sub_pipeline = Pipeline(stages=[custom, tokenizer, hashingTF, lr])
model = sub_pipeline.fit(df)

model.write().overwrite().save("/tmp/my_custom_model")

loaded_model = PipelineModel.load("/tmp/my_custom_model")

loaded_model.transform(df).show()