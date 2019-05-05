package apiclient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.Timeout
import domain.Bar
import play.api.libs.json.{JsValue, Json}
import usecase._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.Source


class AlpacaApiClient(baseUrl: String, keyId: String, secretKey: String) extends ApiClient {

  implicit val timeout = Timeout(5 second)
  implicit val system = ActorSystem()
  implicit val materialize = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def fetchChart(tickers: Seq[String], period: BarPeriod): Map[String, List[Bar]] = {

    val periodUriFragment = period match {
      case OneDay => "/bars/1D"
      case OneMin => "/bars/1Min"
      case FiveMin => "/bars/5Min"
      case FifteenMin => "/bars/15Min"
    }

    val uri = Uri(this.baseUrl + periodUriFragment).withRawQueryString("symbols=" + tickers.mkString(","))
    val h1: RawHeader = RawHeader("APCA-API-KEY-ID", this.keyId)
    val h2: RawHeader = RawHeader("APCA-API-SECRET-KEY", this.secretKey)

    val req = HttpRequest(HttpMethods.GET, uri = uri).withHeaders(List(h1, h2))
    val res = Await.result(Http().singleRequest(req), timeout.duration)
    val body = Unmarshal(res.entity).to[String]

    Await.result(Http().shutdownAllConnectionPools(), timeout.duration)
    Await.result(system.terminate(), timeout.duration)
    val jsValue = Json.parse(Await.result(body, timeout.duration))

    tickers.map(t => (
      t,
      (jsValue \ t).as[List[JsValue]].map(v => Bar.apply(t, v))
    )).toMap
  }

}

object AlpacaApiClient {
  def apply(): AlpacaApiClient = {
    val settings: JsValue = Json.parse(Source.fromResource("settings.json").getLines().mkString)
    val baseUrl: String = (settings \ "endpoint").as[String]
    val keyId: String = (settings \ "keyId").as[String]
    val secretKey: String = (settings \ "secretKey").as[String]
    new AlpacaApiClient(baseUrl, keyId, secretKey)
  }
}