# Sparta examples for developers
This project is aimed at facilitating  any developer the implementation of their own Sparta plugins through showing diverse examples. Featuring all the functionalities included in our SDK, our main goal is that implementing any custom plugin becomes as easy as developing a workflow.

## Getting Started
Before get going you must first `clone` this repository. Bear in mind that all the examples available in this repository were developed using Intellij IDE with `Spark 2.2.0` and `Scala 2.11`.

### Installing

Once you have it cloned  you must create a new Intellij project and  import the repository modules in our new Intellij project
`File > Project structure > Project settings > Modules`
This will allows us to resolve all the dependencies included in the POM examples. You can use any of the plugin examples as a sheet to start developing you own plugin. 
Finally you must import one or both JAR's. Choosing between any of them will depend on whether you have access or not to the Stratio Crossdata library. The `sdk-lite-xd` JAR has a Crossdata dependency in it whereas the `sdk-lite` is implemented solely with Scala & Spark.  

### Developing your custom plugin

#### Create ad-hoc validations

If there are some preconditions that must be met in order to correctly use the plugin, it is possible to add a validate method that follows this signature and return the ValidationResult ATD.
Please note that there is a sequence of messages, so it is possible to specify multiple errors.
```scala
override def validate(): ValidationResult = {
    var validation = ValidationResult(valid = true, messages = Seq.empty)

    if (rawData.isEmpty) {
      validation = ValidationResult(
        valid = false,
        messages = validation.messages :+ "Test data must be set inside the Option properties with an option key named 'raw'")
    }
    validation
  }
```
#### Exploit the properties
Every plugin comes with a properties `(Map[String, String])` parameter where you can retrieve the data from:

    - Any user-defined property inside the Option properties field.
    - All the variables passed inside the tab Writer (without whitespaces and written in camel case)
    
These (key,values) can be defined from the plugin step while building the workflow. 
![properties](https://hydra.stratio.com/wp-content/uploads/sites/14/stratio-sparta398000715.png?raw=true)
    
An example on how to access this data from any input, transformation or output could be as follows:
```scala
  lazy val rawData: Option[String] = properties.get("raw") match {
    case Some(value: String) => Option(value)
    case Some(value) => Option(value.toString)
    case None => None
  }
```
List of values used in the Writer tab:
```
| Name as it appears on the screen | Name as retrieved by the backend |
|:--------------------------------:|:--------------------------------:|
|            Table name            |             tableName            |
|            Primary key           |            primaryKey            |
|           Partition by           |             saveMode             |
|      Unique Constraint Name      |       uniqueConstraintName       |
|     Unique Constraint Fields     |      uniqueConstraintFields      |
|           Update fields          |           updateFields           |
```
Custom output plugin use the list above to configure the save function to be implemented, which will treat the outgoing data. This parameters can be accessed like this:
```scala
/* outputOptions: OutputOptions */
val tableName = outputOptions.tableName.getOrElse{
      logger.error("Table name not defined")
      throw new NoSuchElementException("tableName not found in options")}
```

### Running the tests

In the `transformation-lite-xd` module you will find an example on how to develop a unit test for your custom plugins. The available POM in the test folder has already all the necessary depdendencies to just simply start writing your own test cases  and run them!   

```scala
  "TokenizerTransformStepBatch" should "tokenize the data in and return two values" in {
    val result = tokenizer.transform(Map("custom-transform" -> inBatch)).data.first().toSeq

    result.size shouldBe 2
  }
```
## Generating your JAR and uploading it
Once your code is done and tested you can go ahead and generate your custom JAR by using `mvn clean package`. A JAR will be generated and located in the target folder of our project. You can later upload this JAR using the Sparta UI 


## Deployment
Add additional notes about how to deploy this on a live system

## Built With
* [Maven](https://maven.apache.org/) - Dependency Management

