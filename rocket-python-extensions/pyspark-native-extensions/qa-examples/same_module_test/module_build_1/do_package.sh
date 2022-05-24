#!/usr/bin/env bash

# Current script directory
DIR="$(dirname "$(readlink -f "$0")")"
cd $DIR

# Packaging - Zipping module
zip -r user1_module.zip my_module

# Move packaged artifact to parent folder
mv $DIR/user1_module.zip $DIR/../.