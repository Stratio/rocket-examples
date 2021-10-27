import sys
import os

# => Working directory
wd = os.path.abspath(os.path.dirname(__file__))

# => Setting spark environment ~ Rocket integration
if not os.getenv('SPARK_HOME'):
    os.environ['SPARK_HOME'] = "XXXXXXXXXXX"
spark_home = os.environ.get('SPARK_HOME', None)
# Add pyspark and py4j to path.
sys.path.insert(0, spark_home + "/python")
sys.path.insert(0, os.path.join(spark_home, 'python/lib/py4j-0.10.9-src.zip'))

from pyspark.sql import SparkSession
import mlflow

# => Creating a pyspark session ~ Rocket integration
spark = SparkSession.builder.master("local[*]")\
    .appName("Debugging Spark-Mlflow integration") \
    .config("spark.sql.execution.arrow.pyspark.enabled", "true") \
    .config("spark.sql.execution.pandas.convertToArrowArraySafely", "true") \
    .getOrCreate()

# => Reading data ~ Rocket integration
df = spark.read.csv(
    path=os.path.join(wd, 'data', 'train.csv'),
    header=True,
    inferSchema=True
)

# => Mlflow logged model path ~ Rocket integration
modelDirPath = os.path.join(wd, 'spark_inference', 'model')

# => Input features:
# - extracted from model metadata ~ signature ~ Rocket integration (if your model do not contains a signature,
#   features must be manually defined
inputSignature = [
    {"name": "xxxxx", "type": "yyyy"},
    ...
]
features = [s["name"] for s in inputSignature]

# => Defining Mlflow spark udf output column ~ Rocket integration
predictionColumnName = "prediction"
predictionColumnType = "long"

# => Constructing UDF ~ Rocket integration
prediction_udf = mlflow.pyfunc.spark_udf(spark, modelDirPath, result_type=predictionColumnType)

# => Making predictions ~ Rocket integration
predictionDf = df.withColumn(predictionColumnName, prediction_udf(*features))
predictionDf.show()

