# PySpark native artifacts in Rocket

[Spark dock - using-pyspark-native-features](https://spark.apache.org/docs/3.1.1/api/python/user_guide/python_packaging.html#using-pyspark-native-features)

**Funcionalidad nativa de PySpark:**
 
    Gestión en driver de Python files (.py), zipped Python packages (.zip), y Egg files (.egg), ç
    junto con su distribución a los executors
    
    - Setting the configuration setting spark.submit.pyFiles
    - Setting --py-files option in Spark scripts
    - Directly calling pyspark.SparkContext.addPyFile() in applications


En Rocket se ha integrado mediante la funcionalidad de extensiones; un
administrador puede subir una extension de su maquina a HDFS o puede
registrar una URI (Http o HDFS) apuntando a la extensión.

## Version test

Se han creado 6 extensiones; 3 (una de cada tipo py, egg y zip) se registraran con su URI y 3 (una de cada tipo py, egg y zip) se subirán como fichero para que se almacene en HDFS.
Se ha creado 2 versiones de cada extensión, para probar la funcionalidad de update de extensiones.

**Código de cajita pyspark para poder probarlas:**

```
from pyspark.sql import *
from pyspark.sql.functions import *
from pyspark.sql.types import *

def pyspark_transform(spark, df, param_dict):

    # Importing modules from HTTP artifacts
    import test_pyfile_from_http as py_http
    import test_pyfile_egg_pkg_from_http.test_pyfile_egg as egg_http
    import test_pyfile_zip_pkg_from_http.test_pyfile_zip as zip_http

    # Importing modules from HDFS artifacts
    import test_pyfile_from_hdfs as py_hdfs
    import test_pyfile_egg_pkg_from_hdfs.test_pyfile_egg as egg_hdfs
    import test_pyfile_zip_pkg_from_hdfs.test_pyfile_zip as zip_hdfs

    # ------------------
    #  Driver
    # ------------------

    data = [(
        sys.path, 
        py_http.func_test_pyfile_script("driver"), egg_http.func_test_pyfile_egg("driver"), zip_http.func_test_pyfile_zip("driver"),
        py_hdfs.func_test_pyfile_script("driver"), egg_hdfs.func_test_pyfile_egg("driver"), zip_hdfs.func_test_pyfile_zip("driver")
    )]
    columns = [
        "pythonpath",
        "http_python_script", "http_egg_file", "http_zip_file",
        "hdfs_python_script", "hdfs_egg_file", "hdfs_zip_file"
    ]
    driver_df = spark.createDataFrame(data).toDF(*columns)

    # ------------------
    #  Executor
    # ------------------

    from pyspark.sql.functions import udf
    from pyspark.sql.types import StringType
   
    # from Http
    http_script_udf = udf(lambda x: py_http.func_test_pyfile_script(x), StringType())
    http_egg_udf = udf(lambda x: egg_http.func_test_pyfile_egg(x), StringType())
    http_zip_udf = udf(lambda x: zip_http.func_test_pyfile_zip(x), StringType())

    # from Hdfs
    hdfs_script_udf = udf(lambda x: py_hdfs.func_test_pyfile_script(x), StringType())
    hdfs_egg_udf = udf(lambda x: egg_hdfs.func_test_pyfile_egg(x), StringType())
    hdfs_zip_udf = udf(lambda x: zip_hdfs.func_test_pyfile_zip(x), StringType())

    executor_df = df.withColumn(
        "http_python_script", http_script_udf(df["spark_component"])
        ).withColumn(
            "http_egg_file", http_egg_udf(df["spark_component"])
        ).withColumn(
            "http_zip_file", http_zip_udf(df["spark_component"])
        ).withColumn(
            "hdfs_python_script", hdfs_script_udf(df["spark_component"])
        ).withColumn(
            "hdfs_egg_file", hdfs_egg_udf(df["spark_component"])
        ).withColumn(
            "hdfs_zip_file", hdfs_zip_udf(df["spark_component"])
        )


    return executor_df, driver_df
```

### Servir extensiones en servidor Http

Se pueden copiar a la máquina de bootstrap y desde ahí, pues abrir un servidor de ficheros de python:

    scp -i key test_pyfile_zip_pkg_from_http.zip jk-b80b85884a8849b0b011@bootstrap.xray.labs.stratio.com:/tmp/python_files
    
    Desde la máquina de bootstrap y en la carpeta donde hemos subido los artefactos:
    
        python -m SimpleHTTPServer 6969

    
## Same module test

Son dos artefactos que definen el mismo módulo de python; la idea es que dos workflow distintos los puedan usar en debug, sin que una extension afecte a la otra.

Es para comprobar que aunque la session de Spark sea compartida, existe aislamiento entre las extensiones de tipo python.