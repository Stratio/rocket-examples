import os
import mlflow
from mlflow.tracking import MlflowClient
from mlproject import train

# --------------------------------------------------------------------------------
# Note: This python script must be launched using the virtual environment
#       defined in MLproject through conda.yaml file:
#
# Note: This v.env has been pre-created at venvs/mlproject
# --------------------------------------------------------------------------------

# Current directory
wd = os.path.abspath(os.path.dirname(__file__))

# Reading data ~ Rocket setup
input_csv = os.path.join(wd, 'data', 'train.csv')

# Creating experiment ~ Mlflow launcher
# MLflow 3 no longer accepts a local folder as tracking backend: the runs metadata goes to a SQLite
# database and the artifacts (the logged model) are kept in the same mlflow_runs folder
mlflow_runs = os.path.join(wd, 'mlflow_runs')
tracking_uri = "sqlite:///{}".format(os.path.join(mlflow_runs, 'mlflow.db'))
mlflow.set_tracking_uri(tracking_uri)
client = MlflowClient(tracking_uri=tracking_uri)
experiment_name = "local"
if not client.get_experiment_by_name(experiment_name):
    client.create_experiment(experiment_name, artifact_location="file://{}".format(os.path.join(mlflow_runs, 'artifacts')))
mlflow.set_experiment(experiment_name)


# Executing command defined in MLproject file ~ Mlflow launcher
train.main(
    [
        "--training_data={}".format(input_csv),
        "--feature_column_name={}".format("class"),
        "--prediction_column_name={}".format("prediction")
    ]
)
