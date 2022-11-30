/*
 * © 2017 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written authorization from Stratio Big Data Inc., Sucursal en España.
 */
package com.stratio.rocket.poc.plugins.properties

class ValidatePropertiesMap [K, V](val m: Map[K, V]){
  def getString(key: K): String =
    m.get(key) match {
      case Some(value: String) => value
      case Some(value) => value.toString
      case None =>
        throw new IllegalStateException(s"$key is mandatory")
    }

  def notBlank(option: Option[String]): Boolean =
    option.map(_.trim).forall(_.isEmpty)
}

class NotBlankOption(s: Option[String]) {
  def notBlank: Option[String] = s.map(_.trim).filterNot(_.isEmpty)
}

object ValidatingPropertyMap{
  implicit def map2ValidatingPropertyMap[K, V](m: Map[K, V]): ValidatePropertiesMap[K, V] =
    new ValidatePropertiesMap[K, V](m)

  implicit def option2NotBlankOption(s: Option[String]): NotBlankOption = new NotBlankOption(s)
}