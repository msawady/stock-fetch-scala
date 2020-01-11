package domain

import java.text.{DecimalFormat, NumberFormat}

import org.joda.time.DateTime

case class PriceReport(
    ticker: Ticker,
    asOfPrice: StockPrice,
    asOfDatetime: DateTime,
    previousDateClose: Option[StockPrice]
) {
  val diff: Option[BigDecimal] = previousDateClose.map(asOfPrice - _)

  val ratio: Option[BigDecimal] = previousDateClose.map(asOfPrice % _)
}

object PriceReport {

  def fromCurrentBarAndChart(
      currentBar: Bar,
      dailyChart: Chart
  ): PriceReport = {
    if (dailyChart.latest.exists(_.time == currentBar.time)) {
      // considering after market close
      PriceReport(
        currentBar.ticker,
        currentBar.close,
        currentBar.time,
        dailyChart.beforeLatest.map(_.close)
      )
    } else {
      PriceReport(
        currentBar.ticker,
        currentBar.close,
        currentBar.time,
        dailyChart.latest.map(_.close)
      )
    }
  }
}
