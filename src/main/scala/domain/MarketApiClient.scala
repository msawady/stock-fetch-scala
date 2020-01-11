package domain

import com.twitter.util.Future

trait MarketApiClient {

  def fetchChart(
      tickers: Seq[Ticker],
      period: BarPeriod,
      span: Int = 10
  ): Future[Map[Ticker, Option[Chart]]]

}
