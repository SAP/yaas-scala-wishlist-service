package com.sap.yaas.wishlist.controllers

import com.google.inject.Inject
import com.sap.yaas.wishlist.document.{DocumentClient, DocumentExistsException}
import com.sap.yaas.wishlist.model.{Wishlist, WishlistItem, YaasAwareParameters}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by lutzh on 30.05.16.
  */
class Application @Inject()(documentClient: DocumentClient)
                           (implicit executionContext: ExecutionContext) extends Controller {

  def list = Action { request =>
    Ok(Json.toJson(WishlistItem.dummyItem))
  }

  def create(token: String) = Action.async(BodyParsers.parse.json) { request =>
    val jsresult: JsResult[Wishlist] = request.body.validate[Wishlist]
    jsresult match {
      case wishlistOpt: JsSuccess[Wishlist] =>
        val yaasAwareParameters: YaasAwareParameters = getYaasAwareParameters(request)
        println("wishlist: " + jsresult.get)
        documentClient.create(
          yaasAwareParameters, wishlistOpt.get, token).map(
          response => Ok(Json.toJson(response))
        ).recover({
          case e: DocumentExistsException => Conflict
          case e:Exception =>
            e.printStackTrace()
            InternalServerError(e.getMessage)
        })
      case error: JsError =>
        println("Errors: " + JsError.toJson(error).toString())
        Future(BadRequest)
    }
  }

  def getYaasAwareParameters(request: Request[JsValue]): YaasAwareParameters = {
    // TODO: validation, currently 500
    new YaasAwareParameters(
      request.headers.get("hybris-tenant").get,
      request.headers.get("hybris-client").get,
      request.headers.get("scope").getOrElse(""),
      request.headers.get("hybris-user"),
      request.headers.get("hybris-requestId"),
      request.headers.get("hybris-hop").getOrElse("1").toInt)
  }

  def update = Action(BodyParsers.parse.json) { request =>
    val jsresult: JsResult[WishlistItem] = request.body.validate[WishlistItem]
    jsresult match {
      case _: JsSuccess[WishlistItem] =>
        println("wishlist item: " + jsresult.get)
        Ok
      case error: JsError =>
        println("Errors: " + JsError.toJson(error).toString())
        BadRequest
    }
  }
}
