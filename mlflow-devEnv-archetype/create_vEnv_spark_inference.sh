#!/usr/bin/env bash

WD="$(dirname "$(readlink -f "$0")")"
CONDA_YAML_PATH=$WD/spark_inference/conda.yaml
VENV_PATH=$WD/venvs/spark_inference

echo "Creating conda environment defined in ${CONDA_YAML_PATH} at ${VENV_PATH}"

conda env create -f "$CONDA_YAML_PATH" --prefix "$VENV_PATH"

