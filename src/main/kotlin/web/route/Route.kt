package web.route

import datasource.mapper.MapperDataDomain
import datasource.mapper.MapperDomainData
import datasource.model.GameEntity
import domain.repository.GameRepository
import domain.model.CurrentGame
import domain.model.GameStatus
import domain.service.GameService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import web.mapper.MapperDomainWeb
import web.mapper.MapperWebDomain
import web.model.GameDTO
import java.util.UUID

fun Application.configureRouting() {
    val gameRepository by inject<GameRepository>()
    val gameService by inject<GameService>()
    routing {
        get("/") {
            call.respondText("Server is running")
        }

        post("/game") {
            try {

                val newGame = domain.model.CurrentGame.new()
                gameRepository.save(MapperDomainData.fromDomainToData(newGame))
                val gameDTO = MapperDomainWeb.fromDomainToWeb(newGame)
                call.respond(HttpStatusCode.Created, gameDTO)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        post("/game/{id}") {
            try {
                val gameIdStr = call.parameters["id"] ?: throw IllegalArgumentException("Missing game ID")
                val gameId = UUID.fromString(gameIdStr)
                val moveRequest = call.receive<GameDTO>()

                if (moveRequest.id != gameIdStr) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Game ID mismatch"))
                    return@post
                }

                val currentGame = MapperWebDomain.fromWebToDomain(moveRequest)
                val prevGameEntity = gameRepository.get(gameId)

                if (prevGameEntity == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Game not found"))
                    return@post
                }

                val prevGame = MapperDataDomain.fromDataToDomain(prevGameEntity)
                val status = gameService.checkGameStatus(prevGame)
                if (status != GameStatus.IN_PROGRESS) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Game is already finished"))
                    return@post
                }

                if (gameService.validatePlayerMove(prevGame, currentGame)) {
                    val newGame = gameService.makeComputerMove(currentGame)
                    gameRepository.save(MapperDomainData.fromDomainToData(newGame))
                    val updatedGame = MapperDomainWeb.fromDomainToWeb(newGame)
                    call.respond(HttpStatusCode.OK, updatedGame)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid move"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}