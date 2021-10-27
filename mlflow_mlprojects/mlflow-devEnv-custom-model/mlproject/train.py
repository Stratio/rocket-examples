import argparse
import sys
import pandas as pd
import numpy as np
import mlflow.pyfunc
from mlflow.models.signature import infer_signature
from mlflow.pyfunc import PythonModel


def parse_args(argv):
    """
    Parses python command line input arguments (defined in MLproject file at command section)
    """
    parser = argparse.ArgumentParser(description='Mlflow example')
    parser.add_argument('--training_data', type=str, help='training data set in csv')
    parser.add_argument('--feature_column_name', type=str, help='')
    parser.add_argument('--prediction_column_name', type=str, help='')
    return parser.parse_args(argv)


class CustomModel(PythonModel):

    def __init__(self, feature_col_name, prediction_col_name):
        self.feature_col_name = feature_col_name
        self.prediction_col_name = prediction_col_name

    def dummy_func(self, x):
        return "Dummy code - {}".format(str(x))

    def predict(self, context, model_input):
        if isinstance(model_input, pd.DataFrame):
            return pd.DataFrame(
                np.vectorize(self.dummy_func)(model_input[self.feature_col_name]), columns=[self.prediction_col_name]
            )
        else:
            raise TypeError("Only DataFrame input types are supported")


def main(argv):
    """ Data """
    args = parse_args(argv)
    pd_data = pd.read_csv(args.training_data)

    # Features
    X_train = pd_data[[args.feature_column_name]]

    """ Model """
    model = CustomModel(args.feature_column_name, args.prediction_column_name)

    # Predictions
    y_pred = model.predict({}, X_train)

    signature = infer_signature(X_train, y_pred)
    print("Signature: {}".format(signature))
#   print("Input as spark schema: {}".format(signature.inputs.as_spark_schema()))
#   print("Output as spark schema: {}".format(signature.outputs.as_spark_schema()))

    """ Tracking """
    with mlflow.start_run() as run:
        print('MFlown run {}'.format(run.info))
        mlflow.pyfunc.log_model("model", python_model=model, signature=signature)


if __name__ == '__main__':
    main(sys.argv[1:])
