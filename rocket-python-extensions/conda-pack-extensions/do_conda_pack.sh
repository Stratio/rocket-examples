#!/usr/bin/env bash

# Current script directory
DIR="$(dirname "$(readlink -f "$0")")"
cd $DIR

# Parsing input parameters
while [[ $# -gt 0 ]]; do
  case "$1" in
    -n)
      env_name="$2"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
  shift
done

env_name=${env_name:-"my_env"}
echo "Conda environment name: $env_name"

# Conda yaml path
conda_yaml_path="$DIR/conda.yaml"

# Creating target directories
target_dir="$DIR/target"
target_conda_dir="$target_dir/$env_name"
mkdir $target_dir

# Creating conda env
conda env create -f $conda_yaml_path -p $target_conda_dir

# Packaging conda env
packaged_conda_env="$target_dir/$env_name.tar.gz"

conda pack -p $target_conda_dir -o $packaged_conda_env

# Removing conda env
conda env remove -p $target_conda_dir