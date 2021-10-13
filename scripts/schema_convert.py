#!/usr/bin/env python3

import argparse
import json


def true_str(language: str):
    return 'true' if language == 'scala' else 'True'


def other_type(type_str: str, language: str):
    return f'{type_str.capitalize()}Type{"()" if language == "python" else ""}'


def to_array(fields: list, language: str):
    return f'Seq({",".join(fields)})' if language == 'scala' else f'[{",".join(fields)}]'


def struct_type(fields: list, language: str, name: str = None):
    array_str = to_array(fields, language)
    if name is not None:
        return f'StructField("{name}", StructType({array_str}), {true_str(language)})'
    else:
        return f'StructType({array_str})'


def obtain_schema(node: dict, language: str):
    internal_node = node.get('type')
    if internal_node == 'struct':
        # Root struct
        fields = [obtain_schema(elem, language)
                  for elem in node.get('fields', [])]
        return struct_type(fields, language)
    elif type(internal_node) == dict:
        # Internal struct or array type
        if internal_node.get('type') == 'struct':
            fields = [obtain_schema(elem, language)
                      for elem in internal_node.get('fields', [])]
            return struct_type(fields, language, node.get("name"))
        elif internal_node.get('type') == 'array':
            element_type = internal_node.get('elementType', [])
            if type(element_type) == dict:
                type_str = obtain_schema(element_type, language)
            else:
                type_str = other_type(element_type, language)
            return f'StructField("{node.get("name")}", ArrayType({type_str}), {true_str(language)})'

    else:
        # Other internal type
        return f'StructField("{node.get("name")}", {other_type(internal_node,language)}, {true_str(language)})'


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Convert Rocket debug schema to Scala/Python schema')
    parser.add_argument(
        "--input",
        type=str,
        help="Input file containing debug schema",
        required=True
    )
    parser.add_argument(
        "--language",
        type=str,
        default='scala',
        help="Language of the schema, must be 'scala' or 'python'",
        choices=['python', 'scala']
    )
    args = parser.parse_args()
    with open(args.input) as file:
        print(obtain_schema(json.load(file), args.language))
