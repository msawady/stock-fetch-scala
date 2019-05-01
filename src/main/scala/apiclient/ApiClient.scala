package apiclient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.Timeout
import domain.Bar
import org.joda.time.Period
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Await
import scala.concurrent.duration._


class ApiClient(url: String, keyId: String, secretKey: String) {
  def apply(url: String, keyId: String, secretKey: String): ApiClient = new ApiClient(url, keyId, secretKey)

  implicit val timeout = Timeout(5 second)
  implicit val system = ActorSystem()
  implicit val materialize = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def fetchDailyChart(tickers: Seq[String]): Map[String, List[Bar]] = {

    val params = "symbols=" + tickers.mkString(",")
    val jsValue = fetchChart("/bars/1D", params)
    tickers.map(t => (
      t,
      (jsValue \ t).as[List[JsValue]].map(v => Bar.apply(t, Period.days(1), v))
    )).toMap
  }

  private def fetchChart(path: String, params: String): JsValue = {

    val uri = Uri(this.url + path).withRawQueryString(params)
    val h1: RawHeader = RawHeader("APCA-API-KEY-ID", this.keyId)
    val h2: RawHeader = RawHeader("APCA-API-SECRET-KEY", this.secretKey)

    val req = HttpRequest(HttpMethods.GET, uri = uri).withHeaders(List(h1, h2))
    val res = Await.result(Http().singleRequest(req), timeout.duration)
    val body = Unmarshal(res.entity).to[String]
    system.terminate

    Json.parse(Await.result(body, timeout.duration))
  }
}