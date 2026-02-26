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

fun Route.gameById(gameRepo: GameRepository, userService: UserService) {
    post("/game/{id}") {
        try {
            val principal = call.principal<UserIdPrincipal>()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val playerId = UUID.fromString(principal.name)

            val gameId = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid game id"))
            val game = gameRepo.get(gameId)
                ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Game not found"))

            if (game.player1.id != playerId && game.player2?.id != playerId) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "You are not a participant of this game"))
                return@post
            }

            val gameDTO = MapperDomainWeb.fromDomainToWeb(game)

            call.respond(HttpStatusCode.OK, gameDTO)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
        }
    }
}