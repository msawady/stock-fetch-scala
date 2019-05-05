package usecase

import domain.Bar

trait UsesApiClient {
  val apiClient: ApiClient
}

trait ApiClient {

  def fetchChart(tickers: Seq[String], period: BarPeriod): Map[String, List[Bar]]

}
