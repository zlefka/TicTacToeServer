package web.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateGameRequest(val playerSymbol: WebCell,
                             val isBot: Boolean)
