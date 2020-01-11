package settings

import play.api.libs.json.{JsValue, Json}

import scala.io.Source

case object Settings {
  val settings: JsValue = Json.parse(Source.fromResource("settings.json").getLines().mkString)
  val baseUrl: String = (settings \ "endpoint").as[String]
  val keyId: String = (settings \ "keyId").as[String]
  val secretKey: String = (settings \ "secretKey").as[String]
  val slackToken: String = (settings \ "slackToken").as[String]
  val slackWorkspaceUrl: String = (settings \ "slackWorkspaceUrl").as[String]
  val channelId: String = (settings \ "slackChannelId").as[String]
}

