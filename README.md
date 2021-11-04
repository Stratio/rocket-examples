# Rocket examples for developers
This project is aimed at facilitating  any developer the implementation of their own Sparta plugins or Rocket extensions through showing examples of each kind of step. The repository features all the functionalities included in our SDK. Our main goal is, that implementing any custom plugin becomes as easy as developing a workflow.

## Getting Started
Before get going you must first `clone` this repository. Bear in mind that all the examples available in this repository were developed using Intellij IDE with `Scala 2.12`.

### Installing

Once you have it cloned  you must create a new Intellij project and  import the repository modules in it:
`File > Project structure > Project settings > Modules`. This will allow you to resolve all the dependencies included in the POMs. If you have gotten this step right you will automatically see how all the dependencies start to be resolved in the code.

Finally, you must make sure to import one or both SDK JAR's in every module. You can do this by clicking in the dependency tab and then in the `+` symbol, this will allow you to browse your local filesystem and upload them. Choosing between any of them will depend on whether you have access or not to the Stratio Crossdata library.
The `sdk-lite-xd` JAR has a Crossdata dependency in it whereas the `sdk-lite` is implemented solely with Scala & Spark. Bear in mind that if you decide to use the `sdk-lite-xd` you will also need to upload the `sdk-lite` JAR since it has the latter as a dependency in its POM.

You can use  any of the plugin examples in this repository as a sheet to start developing you own plugin.

### Developing your custom plugin

#### Create ad-hoc validations

If there are some preconditions that must be met in order to correctly use the plugin, it is possible to add a validate method that follows the following syntax and return the ValidationResult ATD.
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
Once we load the JAR and use the appropiate custom step, these validations will be prompted in the Sparta UI as a requirement and wont let us run the workflow until we fullfil them.

#### Exploit the properties
Every plugin comes with a properties `(Map[String, String])` parameter where you can retrieve the data from:

    - Any user-defined property inside the Option properties field.
    - All the variables passed inside the tab Writer (without whitespaces and written in camel case)
    
These (key,values) can be defined from the plugin step while building the workflow.
    
An example on how to access this data from any input, transformation or output could be as follows:
```scala
  lazy val rawData: Option[String] = properties.get("raw") match {
    case Some(value: String) => Option(value)
    case Some(value) => Option(value.toString)
    case None => None
  }
```

Also, custom output plugins use the writer fields to configure the save function to be implemented, which will treat the outgoing data. These parameters can be accessed as follows:
```scala
/* outputOptions: OutputOptions */
val tableName = outputOptions.tableName.getOrElse{
      logger.error("Table name not defined")
      throw new NoSuchElementException("tableName not found in options")}
```

List of accesible values from the Writer tab:
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


### Running the tests

In the `transformation-lite-xd` module you will find an example on how to develop a unit test for your custom plugins. The available POM in the test folder has already all the necessary depdendencies to just simply start writing your own test cases  and run them!   

```scala
  "TokenizerTransformStepBatch" should "tokenize the data in and return two values" in {
    val result = tokenizer.transform(Map("custom-transform" -> inBatch)).data.first().toSeq

    result.size shouldBe 2
  }
```
## Generating your JAR and uploading it
Once your code is done and tested you can go ahead and generate your custom JAR by using `mvn clean package`. A JAR will be generated and located in the target folder of your project. You can later upload this JAR using the Sparta UI. All the JARs in this repository will be stored in your /tmp folder (by default when using Sparta in a cluster these JARs will be stored in HDFS).
![uploadPlugin](https://user-images.githubusercontent.com/7203924/58958966-56134300-87a4-11e9-8f8b-16ac927375b4.png?raw=true)

Later, you must make sure that in the workflow settings the option for 'Add uploaded plugins' is marked. This option will allow the workflow to access all the JARs uploaded in the Plugin repository.
![uploadPlugin](https://user-images.githubusercontent.com/7203924/58958963-557aac80-87a4-11e9-8e03-294620b80c5b.png?raw=true)

Lastly, you will only need to add the matching custom box to your workflow and specify the name of your plugin class. Which of course must be contained in the uploaded JAR.
![addCustom](https://user-images.githubusercontent.com/7203924/58958964-56134300-87a4-11e9-910c-e33c57d266f7.png?raw=true)

Make sure to add the full name with its packaging.

![addCustom](https://user-images.githubusercontent.com/7203924/58958961-557aac80-87a4-11e9-8147-4d763e0bdec2.png?raw=true)

## UDFs and UDAFs integration
Stratio Sparta broadens the utilities available for you in this release in order to add versatility to workflow development. Now UDF’s and UDAF’s are fully integrated into the SDK.

You will only need to extend the traits found in the sdk-lite package in order to start developing your own.

Here’s a simple example of a UDF developed by extending the trait SpartaUDF.
```scala
case class ToUpperCaseUDF() extends SpartaUDF {

  val name = "uppercaseSparta"

  val upper: String => String = _.toUpperCase

  val userDefinedFunction: UserDefinedFunction =
    UserDefinedFunction(upper , StringType, Option(Seq(StringType)))
}
```
After getting this done, it will be necessary to first upload the JAR with the same procedure explained above and then register it through the workflow settings
![addCustom](https://user-images.githubusercontent.com/7203924/58958967-56134300-87a4-11e9-824a-6f360da0c455.png?raw=true)

Lastly, in order to use the newly registered UDF you just need to call it in a Trigger transformation and pass it some valid value.
![UDFUsage](https://user-images.githubusercontent.com/7203924/58961479-827d8e00-87a9-11e9-90f3-c0974068dc9b.png?raw=true)


## Built With
* [Maven](https://maven.apache.org/) - Dependency Management

