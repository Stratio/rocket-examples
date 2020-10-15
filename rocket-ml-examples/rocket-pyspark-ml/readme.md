
### Python package with custom PySpark estimator and transformer

#### How to upload to Nexus

Upload → utilidad twine

    pip instal twine
    
Fichero de configuración para apuntar a repositorio externo:

gedit ~/.pypirc

    [distutils]
    index-servers = pypi
    [pypi]
    repository: https://nexus.s000001.xray.labs.stratio.com/repository/rocket-pip-internal/
    username: admin
    password: 1234

Por linea de comandos:

    twine upload XXX.tar.gz --cert ~/workspace/entornos/xray/ca.crt --verbose

