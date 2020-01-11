package domain

import org.joda.time.DateTime
import play.api.libs.json.JsValue

import scala.language.implicitConversions

case class Bar(
    ticker: Ticker,
    close: StockPrice,
    open: StockPrice,
    high: StockPrice,
    low: StockPrice,
    volume: StockPrice,
    time: DateTime
) {
  def latest(): StockPrice = close
}

object Bar {
  def apply(ticker: Ticker, jsValue: JsValue): Bar = {
    val close = (jsValue \ "c").as[BigDecimal]
    val open = (jsValue \ "o").as[BigDecimal]
    val high = (jsValue \ "h").as[BigDecimal]
    val low = (jsValue \ "l").as[BigDecimal]
    val volume = (jsValue \ "v").as[BigDecimal]
    val time = new DateTime((jsValue \ "t").as[Long] * 1000L) // TODO checking TimeZone
    new Bar(ticker, close, open, high, low, volume, time)
  }

  private implicit def toStockPrice(value: BigDecimal): StockPrice =
    StockPrice(value)
}
