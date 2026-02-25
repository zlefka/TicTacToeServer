package web.route

import domain.model.Cell
import domain.model.CurrentGame
import domain.repository.GameRepository
import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import web.mapper.MapperDomainWeb
import web.model.CreateGameRequest
import web.model.WebCell
import java.util.UUID

fun Route.newGame(gameRepo: GameRepository, userService: UserService) {
    post("/game/new") {
        try {
            val principal = call.principal<UserIdPrincipal>()!!
            val playerId = UUID.fromString(principal.name)

            val request = call.receive<CreateGameRequest>()
            val player = userService.getUserById(playerId)!!
            val isTwoPlayers = !request.isBot
            val botUser = userService.getUserByLogin("computer")
            val computerUser = if (request.isBot) botUser else null

            val playerSymbolDomain = when(request.playerSymbol) {
                WebCell.X -> Cell.X
                WebCell.O -> Cell.O
            }

            val game = CurrentGame.new(
                player = player,
                isTwoPlayers = isTwoPlayers,
                computer = computerUser
            ).copy(player1Symbol = playerSymbolDomain)

            gameRepo.save(game)

            call.respond(HttpStatusCode.Created, MapperDomainWeb.fromDomainToWeb(game))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
        }
    }
}