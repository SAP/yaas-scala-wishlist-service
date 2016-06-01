package com.sap.yaas.wishlist.security

import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

object ManageActionFilter extends ActionFilter[YaasAwareRequest] {
  def filter[A](input: YaasAwareRequest[A]) = Future.successful {
    if (!input.headers.get("scope").contains(SecurityUtils.MANAGE_SCOPE))
      Some(Forbidden)
    else
      None
  }
}