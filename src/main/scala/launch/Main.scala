package launch

import apiclient.{MarketApiClientAlpacaImpl, SlackGatewayImpl}
import usecase.MarketUseCase

object Main {
  def main(args: Array[String]): Unit = {
//    pprint.pprintln(MarketUseCase.fetchDailyChart(Seq("AAPL")).get("AAPL").get.takeRight(10))
    val alpaca = new MarketApiClientAlpacaImpl
    val slack = new SlackGatewayImpl
    pprint.pprintln(
      new MarketUseCase(alpaca, slack).sendReportToSlack(Seq("AAPL")))
  }
}
