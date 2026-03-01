package web.route

import domain.repository.GameRepository
import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import web.mapper.MapperDomainWeb

fun Route.getCurrentGames(gameRepo: GameRepository, userService: UserService) {
    get("/game/available") {
        val principal = call.principal<UserIdPrincipal>()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        val availableGames = gameRepo.getAvailableGames()
        call.respond(availableGames.map { MapperDomainWeb.fromDomainToWeb(it) })
    }
}