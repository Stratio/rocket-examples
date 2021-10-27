import argparse
import sys
import pandas as pd


def parse_args(argv):
    """
    Parses python command line input arguments (defined in MLproject file at command section)
    """
    parser = argparse.ArgumentParser(description='Mlflow example')
    parser.add_argument('--training-data', type=str, help='training data set in csv')
    return parser.parse_args(argv)


def main(argv):
    """ Data """
    args = parse_args(argv)  # mandatory
    pd_data = pd.read_csv(args.training_data)  # mandatory

    """ Tracking """
    with mlflow.start_run() as run:
        print('MFlown run {}'.format(run.info))


if __name__ == '__main__':
    main(sys.argv[1:])
