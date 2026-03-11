package web.route

import domain.repository.GameRepository
import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.response.respond
import web.model.StatisticDTO

fun Route.getStatistic(userService: UserService, gameRepo: GameRepository) {
    get("/game/statistic") {
        val request = call.request.queryParameters["top"]?.toIntOrNull() ?: 10  ///game/statistic?top=4
        if (request < 1) call.respond(HttpStatusCode.BadRequest, "Enter a positive number of leaders")

        val top = gameRepo.getStatistic(request)

        val res = top.map {
            val userById = userService.getUserById(it.userId)

            StatisticDTO(
                userId = userById?.id?.toString() ?: "unknown",
                login = userById?.login ?: "unknown",
                wins = it.wins,
                loses = it.loses,
                draws = it.draws,
                ratio = it.ratio
            )
        }
        call.respond(res)
    }
}