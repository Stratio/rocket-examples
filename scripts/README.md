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

## schema_convert.py
Converts debug schema from Rocket into scala or python schema.

Example

```bash
python3 schema_convert.py --input input.json --language scala
```

```json
# input.json
{
    "type": "struct",
    "fields": [
        {
            "name": "field1",
            "type": "string",
            "nullable": true,
            "metadata": {
                "name": "field1",
                "scale": 0
            }
        },
        {
            "name": "field2",
            "type": "boolean",
            "nullable": true,
            "metadata": {
                "name": "field2",
                "scale": 0
            }
        },
        {
            "name": "field3",
            "type": {
                "type": "array",
                "elementType": "string",
                "containsNull": true
            },
            "nullable": true,
            "metadata": {
                "name": "field3",
                "scale": 0
            }
        },
        {
            "name": "field4",
            "type": {
                "type": "array",
                "elementType": {
                    "type": "struct",
                    "fields": [
                        {
                            "name": "x",
                            "type": "string",
                            "nullable": true,
                            "metadata": {}
                        },
                        {
                            "name": "y",
                            "type": "string",
                            "nullable": true,
                            "metadata": {}
                        },
                        {
                            "name": "z",
                            "type": "string",
                            "nullable": true,
                            "metadata": {}
                        }
                    ]
                },
                "containsNull": true
            },
            "nullable": true,
            "metadata": {
                "name": "field4",
                "scale": 0
            }
        },
        {
            "name": "field5",
            "type": {
                "type": "struct",
                "fields": [
                    {
                        "name": "a",
                        "type": "string",
                        "nullable": true,
                        "metadata": {}
                    },
                    {
                        "name": "b",
                        "type": "string",
                        "nullable": true,
                        "metadata": {}
                    },
                    {
                        "name": "c",
                        "type": "string",
                        "nullable": true,
                        "metadata": {}
                    }
                ]
            },
            "nullable": true,
            "metadata": {}
        }
    ]
}
```

Output in Python:
```python
StructType([StructField("field1", StringType(), True),StructField("field2", BooleanType(), True),StructField("field3", ArrayType(StringType()), True),StructField("field4", ArrayType(StructType([StructField("x", StringType(), True),StructField("y", StringType(), True),StructField("z", StringType(), True)])), True),StructField("field5", StructType([StructField("a", StringType(), True),StructField("b", StringType(), True),StructField("c", StringType(), True)]), True)])
```

Output in Scala:
```scala
StructType(Seq(StructField("field1", StringType, true),StructField("field2", BooleanType, true),StructField("field3", ArrayType(StringType), true),StructField("field4", ArrayType(StructType(Seq(StructField("x", StringType, true),StructField("y", StringType, true),StructField("z", StringType, true)))), true),StructField("field5", StructType(Seq(StructField("a", StringType, true),StructField("b", StringType, true),StructField("c", StringType, true))), true)))
```