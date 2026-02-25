package web.module

import di.gameModule
import io.ktor.server.application.Application
import org.koin.ktor.plugin.Koin
import io.ktor.server.application.install
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(gameModule)
    }
}