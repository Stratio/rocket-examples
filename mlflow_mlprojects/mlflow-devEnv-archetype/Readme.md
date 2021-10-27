# 'Arquetipo' de proyecto para desarrollar/debugear un mlproject de MlFlow

## Estructura

```
.
├── __init__.py
├── create_vEnv_mlproject_command.sh .... Script para crear v.env asociado al script de Python del Mlproject
├── create_vEnv_spark_inference.sh ...... Script para crear v.env asociado al uso de un modelo con pySpark
├── launcher_mlproject_command.py ....... Lanzador para probar/debugear script python del MLproject ~ training process
├── launcher_spark_inference.py ......... Lanzador para probar/debugear uso del modelo en pySpark
├── data ................................ Carpeta de datos
│   └── train.csv ....................... Training dataset en formato csv
├── doc/
├── mlflow_runs/ ........................ Carpeta usada para almacenar el experimeto -> run (ejecución de script de training)
├── mlproject/ .......................... Mlproject a testear (conda.yaml, MLproject file & python code)
│   ├── __init__.py
│   └── train.py ........................ Arquetipo de cómo puede ser la estructura del script de python para su integracion con MLFlow
├── Readme.md
├── spark_inference/ .................... Directorio para contener los artefactos para probar la inferencia en pySpark:
│   ├── conda.yaml ......................   - v.env def (conda.yaml) a usar en la inferencia con pySpark
│   └── model/ ..........................   - el modelo 'logado' usando MlFlow
└── venvs/  ............................. Directorio con los diferentes v.env a usar                       
    ├── launcher/ .......................   - v.env donde se ejecuta el comando "mlflow run ..."
    ├── mlproject/ ......................   - v.env que crea el lanzador "mlflow run ..." usando el conda.yaml del mlproject y donde corre el script definido en la seccion command: "python train.py ..."
    └── spark_inference/ ................   - v.env usado para usar el modelo logado por mlflow en pySpark
```

## Mlflow launcher vs Mlflow run manager & training process logger

Cuando instalas el paquete mlflow en un entorno python, se instala además un script ejecutable para usarlo a través de linea de comandos:

```
(base) asoriano@asoriano:~$ which mlflow
/home/asoriano/miniconda3/bin/mlflow

(base) asoriano@asoriano:~$ cat /home/asoriano/miniconda3/bin/mlflow
#!/home/asoriano/miniconda3/bin/python
# -*- coding: utf-8 -*-
import re
import sys
from mlflow.cli import cli
if __name__ == '__main__':
    sys.argv[0] = re.sub(r'(-script\.pyw|\.exe)?$', '', sys.argv[0])
    sys.exit(cli())

(base) asoriano@asoriano:~$ mlflow --help
Usage: mlflow [OPTIONS] COMMAND [ARGS]...

Options:
  --version  Show the version and exit.
  --help     Show this message and exit.

Commands:
  artifacts    Upload, list, and download artifacts from an MLflow artifact...
  azureml      Serve models on Azure ML.
  db           Commands for managing an MLflow tracking database.
  deployments  Deploy MLflow models to custom targets.
  experiments  Manage experiments.
  gc           Permanently delete runs in the `deleted` lifecycle stage.
  models       Deploy MLflow models locally.
  run          Run an MLflow project from the given URI.
  runs         Manage runs.
  sagemaker    Serve models on SageMaker.
  server       Run the MLflow tracking server.
  ui           Launch the MLflow tracking UI for local viewing of run...
```

Con este script, podemos lanzar un proyecto Mlflow ejecutando "mlflow run ..." (Entorno 1 --> Mlflow como lanzador de proyectos)

1. Mlflow gestiona un experimento (lo inicializa si no existe), donde se almacenarán los metadatos y resultados de la ejecución encapsulados en un 'run' asociado al experimento.
2. Utilizando el conda.yaml definido en el proyecto, crea un nuevo v.env; el nombre que le dá es "mlflow-{hash-contenido-conda.yaml}":
    ```
    (base) asoriano@asoriano:~$ conda env list | grep mlflow
    mlflow-0128e9b926a558e6955757985c4d8e1db85409d0     /home/asoriano/miniconda3/envs/mlflow-0128e9b926a558e6955757985c4d8e1db85409d0
    mlflow-0e5c0ce07638806d5ec1ea3036bb436e4438283e     /home/asoriano/miniconda3/envs/mlflow-0e5c0ce07638806d5ec1ea3036bb436e4438283e
    ```
3. Ejecuta el comando definido en la sección entry_points/{nombre de entrypoint}/command en el nuevo v.env creado; en ese nuevo v.env se debe de instalar otra vez el paquete de mlflow; el usuario utiliza el API python para crear el 'run' y logar metadatos, métricas, modelos.. (Entorno 2 --> Mlflow como gestor de los resultados de un proceso de entrenamiento).

En este proyecto para desarrollo/debug replicamos el proceso de la siguiente manera:

- *create_vEnv_mlproject_command.sh*: con este script creamos el v.env definido en el conda.yaml del proyecto Mlflow en la carpeta local ./venvs/mlproject ~ paso 2 del flujo seguido por el lanzador de Mlflow ("mlflow run ...")
- *launcher_mlproject_command.py*: este script debe ser lanzado utilizando el python del v.env creado; inicializa un experimento en la carpeta local ./mlflow_runs y lanza el commando del entrypoint (esto nos permite debugear el script de training) ~ pasos 1 y 3 del flujo seguido por el lanzador de Mlflow ("mlflow run ...")

## Uso de un modelo con flavour 'python_function' en pySpark

Pre-requisito: tener disponible en el PC una distribución de Spark (https://archive.apache.org/dist/spark/)

Una vez tengamos un modelo logado en Mlflow, podemos comprobar su integración con pySpark:
1. copiaremos el modelo dentro de la carpeta local ./spark_inference/model, definiremos el v.env a usar en el proceso pyspark en ./spark_inference/conda.yaml (basándonos en el conda.yaml interno del MLmodel)
2. ejecutando el script 'create_vEnv_spark_inference.sh' se nos creará el v.env asociado al proceso pyspark en la carpeta local mlflow-devEnv-./venvs/spark_inference
3. ejecutamos el script 'launcher_spark_inference.py' en el v.env creado