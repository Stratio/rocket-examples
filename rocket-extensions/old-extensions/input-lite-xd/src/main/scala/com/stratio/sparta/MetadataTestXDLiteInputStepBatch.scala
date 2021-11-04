/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.sparta

import com.stratio.sparta.sdk.lite.batch.models._
import com.stratio.sparta.sdk.lite.validation.ValidationResult
import com.stratio.sparta.sdk.lite.xd.batch._
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.crossdata.XDSession
import org.apache.spark.sql.types._

import scala.collection.JavaConversions._

class MetadataTestXDLiteInputStepBatch(
                                   xdSession: XDSession,
                                   properties: Map[String, String]
                                 )
  extends LiteCustomXDBatchInput(xdSession, properties) {

  /** Metadata management */

  lazy val metadataSchema = StructType(Seq(StructField("metadataField", StringType)))

  override lazy val metadataDf = xDSession.createDataFrame(Seq(metadataRow.get), metadataSchema)

  override lazy val metadataRow = Some(new GenericRowWithSchema(Array("dummy-metadata"), metadataSchema))

  /** Main plugin functions */

  override def validate(): ValidationResult = ValidationResult(valid = true, messages = Seq.empty)

  override def init(): ResultBatchData = {

    /** On runtime is possible to register rows or any value in metadata cache */

    val exampleCacheKey = s"${name}_status"
    val exampleSchemaForRow = StructType(Seq(StructField("status", IntegerType)))
    val exampleStatusRow = new GenericRowWithSchema(Array(1), exampleSchemaForRow)

    //Is possible to register temporary tables that will be available in other steps
    registerRowMetadataAsTable(exampleCacheKey, exampleStatusRow, exampleSchemaForRow)

    //Ony Spark Rows can be stored in row cache
    addMetadataIntoRowCache(exampleCacheKey, exampleStatusRow)

    //Any value can be stored in custom cache, in the example is used a Map structure
    addMetadataIntoCustomCache(exampleCacheKey, Map("dummy_key" -> exampleStatusRow))

    /** Data returned by custom input */

    val stringSchema = StructType(Seq(StructField("dummy", StringType)))
    val register = Seq(new GenericRowWithSchema(Array("dummy-data"), stringSchema).asInstanceOf[Row])
    val defaultRDD = xdSession.sparkContext.parallelize(register)

    ResultBatchData(defaultRDD, Option(stringSchema))
  }
}
