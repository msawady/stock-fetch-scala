package apiclient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.twitter.util.Future
import domain._
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import settings.Settings

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class MarketApiClientAlpacaImpl() extends MarketApiClient {

  implicit val timeout: Timeout = Timeout(5 second)
  implicit val system: ActorSystem = ActorSystem()
  implicit val materialize: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val baseUrl = Settings.baseUrl
  private val h1 = RawHeader("APCA-API-KEY-ID", Settings.keyId)
  private val h2 = RawHeader("APCA-API-SECRET-KEY", Settings.secretKey)

  import ApiClientBase._

  def fetchChart(tickers: Seq[Ticker],
                 period: BarPeriod,
                 span: Int): Future[Map[Ticker, Option[Chart]]] = {

    val query = Query(
      Map("symbols" -> tickers.map(_.value).mkString(","),
          "limit" -> span.toString)
    )

    val uri = Uri(baseUrl + periodUriFragment(period)).withQuery(query)
    val req = HttpRequest(HttpMethods.GET, uri = uri).withHeaders(List(h1, h2))

    for {
      res <- Http().singleRequest(req).asTwitter
      body <- Unmarshal(res.entity).to[String].asTwitter
    } yield {
      val jsValue = Json.parse(body)

      tickers
        .map(t => {
          val bars = (jsValue \ t.value)
            .asOpt[Seq[JsValue]]
            .map(data => data.map(jv => toDomainBar(t, jv)))
          if (bars.exists(_.nonEmpty)) {
            t -> Some(Chart(bars.get))
          } else {
            t -> None
          }
        })
        .toMap
    }
  }

  private def periodUriFragment(period: BarPeriod) = period match {
    case OneDay     => "/bars/1D"
    case OneMin     => "/bars/1Min"
    case FiveMin    => "/bars/5Min"
    case FifteenMin => "/bars/15Min"
  }

  private def toDomainBar(ticker: Ticker, jsValue: JsValue): Bar = {
    val close = (jsValue \ "c").as[BigDecimal]
    val open = (jsValue \ "o").as[BigDecimal]
    val high = (jsValue \ "h").as[BigDecimal]
    val low = (jsValue \ "l").as[BigDecimal]
    val volume = (jsValue \ "v").as[BigDecimal]
    val time = new DateTime((jsValue \ "t").as[Long] * 1000L) // TODO checking TimeZone
    Bar(ticker, close, open, high, low, volume, time)
  }

  private implicit def toStockPrice(value: BigDecimal): StockPrice =
    StockPrice(value)

  def close(): Future[Unit] = {
    for {
      _ <- Http().shutdownAllConnectionPools().asTwitter
      _ <- system.terminate().asTwitter
    } yield ()
  }
}
