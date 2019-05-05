package usecase

import bindings.MixInApiClient
import domain.Bar

trait MarketUseCase extends UsesApiClient


object MarketUseCase extends MarketUseCase with MixInApiClient {

  def fetchDailyChart(tickers: Seq[String]): Map[String, List[Bar]] = {
    apiClient.fetchChart(tickers, OneDay)
  }
}
