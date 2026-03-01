package web.route

import domain.repository.GameRepository
import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import web.mapper.MapperDomainWeb
import java.util.UUID

fun Route.joinGame(gameRepo: GameRepository, userService: UserService) {
    post("/game/{id}/join") {
        val principal = call.principal<UserIdPrincipal>()
            ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val playerId = UUID.fromString(principal.name)

        val player = userService.getUserById(playerId) ?: return@post call.respond(
            HttpStatusCode.BadRequest,
            mapOf("error" to "Invalid user id")
        )

        val gameId = call.parameters["id"]?.let { UUID.fromString(it) }
            ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid game id"))
        val game = gameRepo.get(gameId)
            ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Game not found"))

        if (game.player1.id != playerId) {
            val updatedGame = game.join(player) ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "You can't join the game"))
            gameRepo.save(updatedGame)
            val response = MapperDomainWeb.fromDomainToWeb(updatedGame)
            call.respond(HttpStatusCode.OK, response)
        } else call.respond(HttpStatusCode.BadRequest, mapOf("error" to "You can't join the game"))
    }
}