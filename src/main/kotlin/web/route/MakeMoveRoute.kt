package web.route

import domain.model.GameBoard.Companion.SIZE
import domain.repository.GameRepository
import domain.service.GameService
import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import web.mapper.MapperDomainWeb
import web.model.MoveRequest
import java.util.UUID

fun Route.makeMove(
    gameRepo: GameRepository,
    userService: UserService,
    computerService: GameService,
    twoPlayersService: GameService
) {
    post("/game/{id}/move") {
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

        if (game.player1.id != playerId && game.player2?.id != playerId) {
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to "You are not a participant of this game"))
            return@post
        }

        val moveRequest = call.receive<MoveRequest>()
        println("Done")
        if (moveRequest.row !in 1..<SIZE  && moveRequest.col !in 1..<SIZE ) return@post call.respond(
            HttpStatusCode.BadRequest,
            mapOf("error" to "Invalid move")
        )
        println("Done2")
        val move = Pair(moveRequest.row, moveRequest.col)
        println("Done3")

        val gameService = if (game.isBot) computerService else twoPlayersService
        println("Done4")
        val updatedGame = gameService.makeMove(game, playerId, move)
        println("Done5")
        gameRepo.save(updatedGame)

        call.respond(MapperDomainWeb.fromDomainToWeb(updatedGame))
    }
}