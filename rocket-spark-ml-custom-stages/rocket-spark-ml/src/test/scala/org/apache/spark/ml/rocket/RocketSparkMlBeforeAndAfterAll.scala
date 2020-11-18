/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package org.apache.spark.ml.rocket

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, Suite}


trait RocketSparkMlBeforeAndAfterAll extends BeforeAndAfterAll { self: Suite =>

  @transient var spark: SparkSession = _
  @transient var sc: SparkContext = _
  //@transient var sqlContext: SQLContext = _

  override def beforeAll() {
    super.beforeAll()
    val sparkMasterIp = System.getProperty("spark.master", "local[2]")
    spark = SparkSession
      .builder().master(sparkMasterIp)
      .appName("RocketSparkMlUnitTest")
      .getOrCreate()
    sc = spark.sparkContext
  }

  override def afterAll() {
    if(spark != null)
      spark.stop()
    super.afterAll()
  }
}
