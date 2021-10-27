#!/usr/bin/env bash

WD="$(dirname "$(readlink -f "$0")")"
VENVS_PATH=$WD/venvs
MLFLOW_RUNS_PATH=$WD/mlflow_runs

read -p "Executing rm -rf ${MLFLOW_RUNS_PATH}/* - (y/n)?" choice
case "$choice" in
y | Y)
  rm -rf "${MLFLOW_RUNS_PATH:?}"/*
  rm -rf "${MLFLOW_RUNS_PATH:?}"/.trash
  ;;
*)
  echo "Skipping"
  ;;
esac

read -p "Executing rm -rf ${VENVS_PATH}/{launcher & mlproject & spark_inference}/* - (y/n)?" choice
case "$choice" in
y | Y)
  rm -rf "${VENVS_PATH:?}"/launcher/*
  rm -rf "${VENVS_PATH:?}"/mlproject/*
  rm -rf "${VENVS_PATH:?}"/spark_inference/*
  ;;
*)
  echo "Skipping"
  ;;
esac

