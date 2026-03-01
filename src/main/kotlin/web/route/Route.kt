package web.route

import domain.repository.GameRepository
import domain.service.GameService
import domain.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.koin.core.qualifier.named
import org.koin.ktor.ext.getKoin

fun Application.configureRouting() {
    val userService: UserService = getKoin().get()
    val gameRepo: GameRepository = getKoin().get()
    val computerService = getKoin().get<GameService>(named("computer"))
    val twoPlayersService = getKoin().get<GameService>(named("twoPlayers"))
    routing {
        root()
        signUpRoute(userService)
        loginRoute(userService)

        authenticate("auth-basic") {
            newGame(gameRepo, userService)
            gameById(gameRepo, userService)
            getCurrentGames(gameRepo, userService)
            joinGame(gameRepo, userService)
            makeMove(gameRepo, userService, computerService, twoPlayersService)
            getUserInfo(userService)
        }
    }
}