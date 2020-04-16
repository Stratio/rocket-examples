/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package org.apache.spark.sql.sparta.udf

import com.stratio.sparta.sdk.lite.common.SpartaUDF
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.ml.linalg.VectorUDT

case object VectorUDT extends VectorUDT

case class GetDenseVectorUDF() extends SpartaUDF {

  val name = "get_vector_ith_element"

  val getVectorElement = (vector: org.apache.spark.ml.linalg.Vector, num: Int) => vector(num)

  val userDefinedFunction: UserDefinedFunction =
    UserDefinedFunction(getVectorElement , DoubleType, Option(Seq(VectorUDT, IntegerType)))
}



