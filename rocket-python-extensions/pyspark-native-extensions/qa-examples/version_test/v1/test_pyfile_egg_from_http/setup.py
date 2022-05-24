from setuptools import setup, find_packages

setup(
    name='test_pyfile_egg_pkg_from_http',
    version='0.1.0',
    description='A short description',
    long_description='A long description',
    author='Rocket',
    author_email='rocket@stratio.com',
    packages=find_packages(exclude=['*tests*']),
)
