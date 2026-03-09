package web.module

import di.gameModule
import io.ktor.server.application.Application
import org.koin.ktor.plugin.Koin
import io.ktor.server.application.install
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val jwtConfig = environment.config.config("jwt")

    install(Koin) {
        slf4jLogger()

        properties(
            mapOf(
                "jwt.secret" to jwtConfig.property("secret").getString(),
                "jwt.audience" to jwtConfig.property("audience").getString(),
                "jwt.issuer" to jwtConfig.property("issuer").getString(),
                "jwt.realm" to jwtConfig.property("realm").getString(),
                "jwt.accessTokenValidity" to jwtConfig.property("accessTokenValidity").getString().toLong(),
                "jwt.refreshTokenValidity" to jwtConfig.property("refreshTokenValidity").getString().toLong()
            )
        )

        modules(gameModule)
    }
}