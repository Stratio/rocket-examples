import sys
import os

# => Working directory
wd = os.path.abspath(os.path.dirname(__file__))

# => Setting spark environment ~ Rocket integration
if not os.getenv('SPARK_HOME'):
    os.environ['SPARK_HOME'] = "/home/asoriano/workspace/software/stratio-spark-distribution-3.1.1-1.2.0-766b881-bin"
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
    .getOrCreate()

# => Reading data ~ Rocket integration
df = spark.read.csv(
    path=os.path.join(wd, 'data', 'train.csv'),
    header=True,
    inferSchema=True
)

# => Mlflow logged model path ~ Rocket integration
modelDirPath = os.path.join(wd, 'spark_inference', 'model')
# · Loading model
loaded_model = mlflow.pyfunc.load_model(modelDirPath)


# => Constructing UDF ~ Rocket integration
#     · We need input features, output column name and output column type
features = None
output_spark_schema = None

# Try to use model signature to infer this parameters
if loaded_model.metadata.signature:
    # Input features
    input_signature = loaded_model.metadata.signature.inputs
    features = [s.name for s in input_signature.inputs]
    # Output column name & type
    output_signature = loaded_model.metadata.signature.outputs
    output_spark_schema = output_signature.as_spark_schema()


# · Input features
if not features:
    features = []
print("Input features for UDF: {}".format(features))

# · Output column name & type
if not output_spark_schema:
    predictionColumnName = "prediction"
    predictionColumnType = "string"
else:
    print("Spark schema: {}".format(output_spark_schema))
    predictionColumnName = output_spark_schema[0].name
    predictionColumnType = output_spark_schema[0].dataType

print("Prediction column name for UDF: {}".format(predictionColumnName))
print("Prediction column type for UDF: {}".format(predictionColumnType))

prediction_udf = mlflow.pyfunc.spark_udf(spark, modelDirPath, result_type=predictionColumnType)

# => Making predictions ~ Rocket integration
predictionDf = df.withColumn(predictionColumnName, prediction_udf(*features))
predictionDf.show()

