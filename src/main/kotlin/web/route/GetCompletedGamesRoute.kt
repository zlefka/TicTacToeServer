package web.route

import domain.repository.GameRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import web.mapper.MapperDomainWeb
import java.util.UUID

fun Route.userGameHistoryRoute(gameRepo: GameRepository) {
    get("/user/me/history") {
        val principal = call.principal<UserIdPrincipal>()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        val userId = UUID.fromString(principal.name)
        val games = gameRepo.getCompletedGames(userId).map { MapperDomainWeb.fromDomainToWeb(it) }

        call.respond(games)
    }

}