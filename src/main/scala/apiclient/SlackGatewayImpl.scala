package apiclient

import java.text.DecimalFormat

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.twitter.util.Future
import domain.{PriceReport, SlackGateway}
import settings.Settings
import slack.api.SlackApiClient

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class SlackGatewayImpl() extends SlackGateway {

  import ApiClientBase._

  private lazy val client =
    SlackApiClient.apply(Settings.slackToken, Settings.slackWorkspaceUrl)

  implicit val timeout: Timeout = Timeout(5 second)
  implicit val system: ActorSystem = ActorSystem()
  implicit val materialize: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val diffFormat = new DecimalFormat("↑#,##0.00;↓#")

  private val percentDiffFormat = new DecimalFormat("↑##0.00%;↓#%")

  private def formatDiffNumber(pr: PriceReport) = {
    s"${pr.diff.map(diffFormat.format(_)).getOrElse("-")} (${pr.ratio.map(percentDiffFormat.format(_)).getOrElse("-")})"
  }

  private def formatPriceReport(priceReport: Seq[PriceReport]): String = {

    priceReport
      .map(pr =>
        s"${pr.ticker.value}: ${pr.asOfPrice.value}  ${formatDiffNumber(pr)}")
      .mkString("\n")
  }

  override def postPriceReport(priceReport: Seq[PriceReport]): Future[Unit] = {
    for {
      _ <- client
        .postChatMessage(Settings.channelId, formatPriceReport(priceReport))
        .asTwitter
    } yield ()
  }
}
