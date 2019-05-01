package launch

import apiclient.ApiClient
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {
    val settings: JsValue = Json.parse(Source.fromResource("settings.json").getLines().mkString)
    val url: String = (settings \ "endpoint").as[String]
    val keyId: String = (settings \ "keyId").as[String]
    val secretKey: String = (settings \ "secretKey").as[String]

    val cli = new ApiClient(url, keyId, secretKey)
    println(cli.fetchDailyChart(Seq("AAPL")))
  }
}
