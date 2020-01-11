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

  val diffStr: Option[String] = diff.map(PriceReport.diffFormat.format(_))

  val diffStrOrHyphen: String =
    diff.map(PriceReport.diffFormat.format(_)).getOrElse("-")

  val ratio: Option[BigDecimal] = previousDateClose.map(asOfPrice % _)

  val percentStr: Option[String] =
    ratio.map(PriceReport.percentDiffFormat.format(_))

  val percentStrOrHyphen: String =
    ratio.map(PriceReport.percentDiffFormat.format(_)).getOrElse("-")
}

object PriceReport {

  private val diffFormat = new DecimalFormat("+#,##0.00;-#")

  private val percentDiffFormat = new DecimalFormat("+##0.00%;-#%")

  def fromCurrentBarAndChart(currentBar: Bar,
                             dailyChart: Chart): PriceReport = {
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
