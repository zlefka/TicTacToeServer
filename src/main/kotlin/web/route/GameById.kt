package web.route

import domain.repository.GameRepository
import domain.service.GameService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import web.mapper.MapperDomainWeb
import web.model.GameDTO
import java.util.UUID
import kotlin.getValue

fun Route.gameById(gameRepo: GameRepository) {
    post("/game/{id}") {
        try {
            val gameId = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid game id"))

            val game = gameRepo.get(gameId)
                ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Game not found"))

            call.respond(HttpStatusCode.OK, MapperDomainWeb.fromDomainToWeb(game))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
        }
    }
}