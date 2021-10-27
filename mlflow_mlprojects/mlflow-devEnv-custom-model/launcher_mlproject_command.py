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
mlflow.set_tracking_uri("file://{}".format(os.path.join(wd, 'mlflow_runs/')))
client = MlflowClient(
    tracking_uri="file://{}".format(os.path.join(wd, 'mlflow_runs'))
)
experiment_name = "local"
if not client.get_experiment_by_name(experiment_name):
    client.create_experiment(experiment_name)


# Executing command defined in MLproject file ~ Mlflow launcher
train.main(
    [
        "--training_data={}".format(input_csv),
        "--feature_column_name={}".format("class"),
        "--prediction_column_name={}".format("prediction")
    ]
)
