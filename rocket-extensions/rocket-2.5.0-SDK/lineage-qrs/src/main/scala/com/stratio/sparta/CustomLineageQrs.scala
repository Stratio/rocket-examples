/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.sparta

object CustomLineageQrs {

  def getHDFSMetadata(options: Map[String, String]): Map[String, String] =
    Map(
      "metadataPath" -> "s000001-hdfs1.s000001://tmp/tablaVolcado>/:tablaVolcado:",
      "dataStoreType" -> "HDFS"
    )

  def getHDFSMetadataFromOptions(options: Map[String, String]): Map[String, String] = {
    val path = options.getOrElse("path", "/tmp")
    val metadataPath = options.get("tableName") match {
      case Some(tableName) =>
        s"s000001-hdfs1.s000001:/$path/$tableName>/:$tableName:"
      case None =>
        val file = path.split("/").last
        s"s000001-hdfs1.s000001:/$path>/:$file:"
    }

    Map(
      "metadataPath" -> metadataPath,
      "dataStoreType" -> "HDFS"
    )
  }

  def getJDBCMetadata(options: Map[String, String]): Map[String, String] =
    Map(
      "metadataPath" -> "s000001-postgresqa.s000001://rocket-nightly>/:enriched.users_with_diagnosis:",
      "dataStoreType" -> "SQL"
    )

  def getJDBCMetadataFromOptions(options: Map[String, String]): Map[String, String] = {
    (options.get("tableName"), options.get("dbtable")) match {
      case (Some(tableName), _) =>
        Map(
          "metadataPath" -> s"s000001-postgresqa.s000001://rocket-nightly>/:$tableName:",
          "dataStoreType" -> "SQL"
        )

      case (_, Some(sparkDbTable)) =>
        Map(
          "metadataPath" -> s"s000001-postgresqa.s000001://rocket-nightly>/:$sparkDbTable:",
          "dataStoreType" -> "SQL"
        )

      case _ =>
        Map.empty
    }

  }
}
