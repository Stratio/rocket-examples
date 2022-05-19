/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.sparta

object CustomLineageQrs {

  def getGovernanceMetadata(options: Map[String, String]): Map[String, String] =
    Map(
      "metadataPath" -> "s000001-hdfs1.s000001://tmp/tablaVolcado>/:tablaVolcado:",
      "dataStoreType" -> "HDFS"
    )
}
