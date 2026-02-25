package web.route

import domain.repository.GameRepository
import domain.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.koin.ktor.ext.getKoin

fun Application.configureRouting() {
    val userService: UserService = getKoin().get()
    val gameRepo: GameRepository = getKoin().get()
    routing {
        root()
        signUpRoute(userService)
        loginRoute(userService)

        authenticate("auth-basic") {
            newGame(gameRepo, userService)
            gameById(gameRepo, userService)
        }
    }
}