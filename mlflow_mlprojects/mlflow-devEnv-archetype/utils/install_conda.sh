#!/usr/bin/env bash

# · Install conda 4.8.3
CONDA_DIR=/opt/conda

curl -LO http://repo.continuum.io/miniconda/Miniconda3-py37_4.8.3-Linux-x86_64.sh
mkdir -p $CONDA_DIR
bash Miniconda3-py37_4.8.3-Linux-x86_64.sh -f -b -p $CONDA_DIR
rm Miniconda3-py37_4.8.3-Linux-x86_64.sh
conda install --quiet --yes conda==4.8.3

# · Configuring conda
conda config --system --set auto_update_conda false
