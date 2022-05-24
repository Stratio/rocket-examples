
# Empaquetarlo en tar.gz

python3 setup.py sdist

# Usarlo en un workflow

Conda.yaml

```
name: rocket-custom

channels:
  - defaults

dependencies:
  - python=3.7.6
  - pip=20.2.2
  - pip:
      - mlflow==1.15.0
      - pyspark==3.1.1
      - pyarrow==5.0.0
      - scikit-learn==0.22.1
      - rocket-python-examples==0.1.0
```

En un step de PySpark:

```
from pyspark.sql import *
from pyspark.sql.functions import *
from pyspark.sql.types import *

def pyspark_transform(spark, df, param_dict):

    from rocket_python_examples.test import dummy_func

    convertUDF = udf(lambda z: dummy_func(z)) 

    return df.withColumn("driverPython", lit(dummy_func("python"))).withColumn("executorPython", convertUDF(df["class"]))
```

