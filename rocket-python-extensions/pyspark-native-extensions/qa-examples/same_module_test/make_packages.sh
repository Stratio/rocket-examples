#!/usr/bin/env bash

# Current script directory
DIR="$(dirname "$(readlink -f "$0")")"
cd $DIR

./module_build_1/do_package.sh
./module_build_2/do_package.sh