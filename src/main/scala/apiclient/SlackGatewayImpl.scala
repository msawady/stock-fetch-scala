package apiclient

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.twitter.util.{Future, FuturePool}
import domain.{PriceReport, SlackGateway}
import settings.Settings
import slack.api.SlackApiClient

import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

class SlackGatewayImpl() extends SlackGateway {

  import ApiClientBase._

  private lazy val client =
    SlackApiClient.apply(Settings.slackToken, Settings.slackWorkspaceUrl)

  implicit val timeout: Timeout = Timeout(5 second)
  implicit val system: ActorSystem = ActorSystem()
  implicit val materialize: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private def formatPriceReport(priceReport: Seq[PriceReport]): String = {
    def separater = "------------------\n"
    priceReport
      .map(pr =>
        s"${pr.ticker}: ${pr.asOfPrice} ${pr.diffStrOrHyphen}(${pr.percentStrOrHyphen})\n")
      .mkString(separater)
  }

  override def postPriceReport(priceReport: Seq[PriceReport]): Future[Unit] = {
    for {
      _ <- client
        .postChatMessage(Settings.channelId, formatPriceReport(priceReport))
        .asTwitter

    } yield ()
  }
}
