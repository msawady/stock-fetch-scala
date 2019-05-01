package domain

import org.joda.time.{DateTime, Period}
import play.api.libs.json.JsValue

case class Bar(
                ticker: String,
                term: Period,
                close: BigDecimal,
                open: BigDecimal,
                high: BigDecimal,
                low: BigDecimal,
                volume: BigDecimal,
                time: DateTime
              )

object Bar {
  def apply(
             ticker: String,
             term: Period,
             close: BigDecimal,
             open: BigDecimal,
             high: BigDecimal,
             low: BigDecimal,
             volume: BigDecimal,
             time: DateTime
           ): Bar = new Bar(ticker, term, close, open, high, low, volume, time)

  def apply(ticker: String, term: Period, jsValue: JsValue): Bar = {
    val close = (jsValue \ "c").as[BigDecimal]
    val open = (jsValue \ "o").as[BigDecimal]
    val high = (jsValue \ "h").as[BigDecimal]
    val low = (jsValue \ "l").as[BigDecimal]
    val volume = (jsValue \ "v").as[BigDecimal]
    val time = new DateTime((jsValue \ "t").as[Long] * 1000L)
    new Bar(ticker, term, close, open, high, low, volume, time)
  }
}
