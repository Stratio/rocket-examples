#!/usr/bin/env python3.6
#
# © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
#
# This software – including all its source code – contains proprietary
# information of Stratio Big Data Inc., Sucursal en España and
# may not be revealed, sold, transferred, modified, distributed or
# otherwise made available, licensed or sublicensed to third parties;
# nor reverse engineered, disassembled or decompiled, without express
# written authorization from Stratio Big Data Inc., Sucursal en España.
#

import os

from setuptools import setup

pjoin = os.path.join

here = os.path.abspath(os.path.dirname(__file__))

packages = []
for d, _, _ in os.walk('rocket_pyspark_ml'):
    if os.path.exists(pjoin(d, '__init__.py')):
        packages.append(d.replace(os.path.sep, '.'))


def setup_package():
    metadata = dict(
        name='rocket_pyspark_ml',
        packages=packages,
        description="""Rocket Pyspark ml""",
        long_description="Stratio Rocket - PySpark ml custom stages",
        author="Stratio Rocket",
        platforms="Linux",
        install_requires=[],
        version="0.1.0",
        keywords=['Rocket', 'PySpark', "ML"],
        classifiers=['Programming Language :: Python :: 3.7'],
    )

    setup(**metadata)


if __name__ == '__main__':
    setup_package()
