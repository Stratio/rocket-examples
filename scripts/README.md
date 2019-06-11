# Scripts

This folder constains useful scripts to be used with Sparta.

## sso.py

This script can be used to obtain the needed cookies to connect with Sparta. It can be used to  
automate the connection with Sparta Swagger API.  

Usage example:  

```bash
$ python3 sso.py --url https://sparta.labs.stratio.com/sparta --user user --password password
```

Example output:

```bash
Cookie: mesosphere_server_id=xxx; user=xxx
```

This output automatically sets the syntax for your curls header:

```bash
#!/bin/bash
cookies=$(python3 sso.py --url https://sparta.labs.stratio.com/sparta --user user --password password)
curl -XGET -kL -H $cookies https://sparta.labs.stratio.com/sparta/workflows
```
