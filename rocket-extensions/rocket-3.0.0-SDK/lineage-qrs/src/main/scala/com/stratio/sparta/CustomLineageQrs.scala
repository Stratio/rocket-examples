/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.sparta

import akka.event.slf4j.SLF4JLogging
import com.stratio.connectors.sscccommons.rocket.{CustomLineage, CustomLineageResult, CustomPlannedQualityRule, CustomPlannedQualityRuleResult}

object CustomLineageQrs extends SLF4JLogging {

  def getHDFSMetadata(customLineage: CustomLineage): CustomLineageResult =
    CustomLineageResult(
      metadataPath = "s000001-hdfs1.s000001://tmp/tablaVolcado>/:tablaVolcado:",
      dataStoreType = "HDFS"
    )

  def getHDFSMetadataFromOptions(customLineage: CustomLineage): CustomLineageResult = {
    val path = customLineage.options.getOrElse("path", "/tmp")
    val metadataPath = customLineage.tableName match {
      case Some(tableName) =>
        s"s000001-hdfs1.s000001:/$path/$tableName>/:$tableName:"
      case None =>
        val file = path.split("/").last
        s"s000001-hdfs1.s000001:/$path>/:$file:"
    }

    CustomLineageResult(
      metadataPath = metadataPath,
      dataStoreType = "HDFS"
    )
  }

  def getJDBCMetadata(customLineage: CustomLineage): CustomLineageResult =
    CustomLineageResult(
      metadataPath = "s000001-postgresqa.s000001://rocket-nightly>/:enriched.users_with_diagnosis:",
      dataStoreType = "SQL"
    )

  def getJDBCMetadataFromOptions(customLineage: CustomLineage): CustomLineageResult = {
    (customLineage.tableName, customLineage.options.get("dbtable")) match {
      case (Some(tableName), _) =>
        CustomLineageResult(
          metadataPath = s"s000001-postgresqa.s000001://rocket-nightly>/:$tableName:",
          dataStoreType = "SQL"
        )

      case (_, Some(sparkDbTable)) =>
        CustomLineageResult(
          metadataPath = s"s000001-postgresqa.s000001://rocket-nightly>/:$sparkDbTable:",
          dataStoreType = "SQL"
        )

      case _ =>
        throw new Exception("Invalid custom properties, missing dbtable option")
    }

  }

  /**
   * Since we do not want to have table names collisions,
   * we should pass the name for the new table to the method inside the object CustomPlannedQualityRule
   * */

  def getCreateTableFromOptions(customCreateTableQrs: CustomPlannedQualityRule): CustomPlannedQualityRuleResult = {
    if (customCreateTableQrs.tableName.isEmpty)
      throw new RuntimeException("Cannot create table because tableName is empty")
    else {
      log.info(s"Trying to create table ${customCreateTableQrs.tableName} with driver ${customCreateTableQrs.driver}, uri ${customCreateTableQrs.uri}, metadatapath ${customCreateTableQrs.metadataPath} securityOptions ${customCreateTableQrs.securityOptions.options} and secType ${customCreateTableQrs.securityOptions.`type`}, datastoreProps ${customCreateTableQrs.datastoreProperties} " +
        s", resourceProperties ${customCreateTableQrs.resourceProperties}, governanceProperties ${customCreateTableQrs.governanceProperties}")
      CustomPlannedQualityRuleResult(createTable = s"CREATE TABLE ${customCreateTableQrs.tableName} as select '${customCreateTableQrs.metadataPath}' as metadataPath, '${customCreateTableQrs.securityOptions.`type`}' as security, '${customCreateTableQrs.extraProperties}' as extraOptions")
    }

  }
}
