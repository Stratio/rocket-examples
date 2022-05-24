#!/usr/bin/env bash

# Current script directory
DIR="$(dirname "$(readlink -f "$0")")"
cd $DIR

./test_pyfile_egg_from_hdfs/do_package.sh
./test_pyfile_egg_from_http/do_package.sh
./test_pyfile_zip_from_hdfs/do_package.sh
./test_pyfile_zip_from_http/do_package.sh