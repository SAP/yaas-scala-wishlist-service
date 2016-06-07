/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.sap.yaas.wishlist.model

import java.util.regex.Pattern

import com.sap.cloud.yaas.servicesdk.patternsupport.traits.YaasAwareTrait.Headers._
import play.api.mvc.Request

/**
  * Header propagation helper to make sure the SAP Hybris required headers are properly passed and set
  */
case class YaasAwareParameters(hybrisTenant: String, hybrisClient: String,
                               hybrisScopes: String,
                               hybrisUser: Option[String],
                               hybrisRequestId: Option[String],
                               hybrisHop: Int = 1) {
  val asSeq: Seq[(String, String)] = Seq(TENANT -> hybrisTenant,
    CLIENT -> hybrisClient,
    HOP -> hybrisHop.toString) ++
    (if (hybrisUser.isDefined) Seq(USER -> hybrisUser.get) else Seq()) ++
    (if (hybrisRequestId.isDefined) Seq(REQUEST_ID -> hybrisRequestId.get) else Seq())
}

object YaasAwareParameters {
  private val DEFAULT_HOP = 1

  def apply[A](request: Request[A]): YaasAwareParameters = {
    new YaasAwareParameters(
      extractHeaderWithValidation(request, TENANT, TENANT_PATTERN),
      extractHeaderWithValidation(request, CLIENT, CLIENT_PATTERN),
      request.headers.get(SCOPES).getOrElse(""),
      request.headers.get(USER),
      request.headers.get(REQUEST_ID),
      extractIntHeader(request, HOP).getOrElse(DEFAULT_HOP))
  }

  def extractHeaderWithValidation(request: Request[Any], headerName: String, pattern: Pattern): String = {
    val header = request.headers.get(headerName)
    if (header.isEmpty || header.get.trim.isEmpty) {
      throw new MissingHeaderException(headerName)
    } else if (!pattern.matcher(header.get).matches()) {
      throw new MalformedHeaderException(headerName)
    }
    header.get
  }

  def extractIntHeader(request: Request[Any], headerName: String): Option[Int] = {
    try {
      request.headers.get(headerName).map(header => header.toInt)
    } catch {
      case e: NumberFormatException => throw new MalformedHeaderException(headerName)
    }
  }

}

class MissingHeaderException(val headerName: String) extends Exception

class MalformedHeaderException(val headerName: String) extends Exception