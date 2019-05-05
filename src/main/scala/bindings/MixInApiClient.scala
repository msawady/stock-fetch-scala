package bindings

import apiclient.AlpacaApiClient
import usecase.UsesApiClient

trait MixInApiClient extends UsesApiClient {
  val apiClient = AlpacaApiClient()
}
