package usecase

import com.twitter.util.Future
import domain._

class MarketUseCase(apiClient: MarketApiClient, slackGateway: SlackGateway) {

  def fetchDailyChart(
      tickers: Seq[String]): Future[Map[Ticker, Option[Chart]]] = {
    apiClient.fetchChart(tickers.map(Ticker), OneDay, 100)
  }

  def fetchPriceData(
      tickers: Seq[String]): Future[Map[Ticker, Option[PriceReport]]] = {
    for {
      minuteChart <- apiClient.fetchChart(tickers.map(Ticker), OneMin, 1)
      dailyChart <- apiClient.fetchChart(tickers.map(Ticker), OneDay, 2)
    } yield {
      minuteChart.map {
        case (ticker, Some(chart)) => {
          val currentBar = chart.latest
          ticker -> dailyChart
            .get(ticker)
            .flatten
            .flatMap(chart => {
              currentBar.map(PriceReport.fromCurrentBarAndChart(_, chart))
            })
        }
        case (ticker, None) => {
          ticker -> None
        }
      }
    }
  }

  def sendReportToSlack(tickers: Seq[String]): Future[Unit] = {
    for {
      data <- fetchPriceData(tickers)
      _ <- slackGateway.postPriceReport(data.values.flatten.toSeq)
    } yield println(s"maybe send $data")
  }
}
