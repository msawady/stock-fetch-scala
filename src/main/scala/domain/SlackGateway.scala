package domain

import com.twitter.util.Future

trait SlackGateway {

  def postPriceReport(priceReport: Seq[PriceReport]): Future[Unit]
}
