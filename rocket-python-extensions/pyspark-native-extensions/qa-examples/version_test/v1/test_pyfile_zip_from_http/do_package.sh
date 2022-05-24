#!/usr/bin/env bash

# Current script directory
DIR="$(dirname "$(readlink -f "$0")")"
cd $DIR

# Packaging - Zipping module
zip -r test_pyfile_zip_pkg_from_http.zip test_pyfile_zip_pkg_from_http

# Move packaged artifact to parent folder
mv $DIR/test_pyfile_zip_pkg_from_http.zip $DIR/../.