#!/usr/bin/env bash

# Current script directory
DIR="$(dirname "$(readlink -f "$0")")"
cd $DIR

# Packaging
python3 setup.py sdist bdist_egg

# Move packaged artifact to parent folder
mv $DIR/dist/test_pyfile_egg_pkg_from_hdfs-0.1.0-py*.egg $DIR/../.

# Remove folders created during packaging
rm -rf build
rm -rf dist
rm -rf test_pyfile_egg_pkg_from_hdfs.egg-info