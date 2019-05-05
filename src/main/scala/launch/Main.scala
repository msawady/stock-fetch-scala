package launch

import usecase.MarketUseCase

object Main {
  def main(args: Array[String]): Unit = {
    pprint.pprintln(MarketUseCase.fetchDailyChart(Seq("AAPL")))
  }
}
