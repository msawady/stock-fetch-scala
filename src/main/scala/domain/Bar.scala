package domain

import org.joda.time.DateTime

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
