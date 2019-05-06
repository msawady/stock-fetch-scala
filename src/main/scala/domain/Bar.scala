package domain

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class Bar(
                ticker: String,
                close: BigDecimal,
                open: BigDecimal,
                high: BigDecimal,
                low: BigDecimal,
                volume: BigDecimal,
                time: DateTime
              )

object Bar {
  def apply(ticker: String, jsValue: JsValue): Bar = {
    val close = (jsValue \ "c").as[BigDecimal]
    val open = (jsValue \ "o").as[BigDecimal]
    val high = (jsValue \ "h").as[BigDecimal]
    val low = (jsValue \ "l").as[BigDecimal]
    val volume = (jsValue \ "v").as[BigDecimal]
    val time = new DateTime((jsValue \ "t").as[Long] * 1000L)
    new Bar(ticker, close, open, high, low, volume, time)
  }
}
