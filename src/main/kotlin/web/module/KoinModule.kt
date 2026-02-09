package web.module

import di.gameModule
import io.ktor.server.application.Application
import org.koin.ktor.plugin.Koin
import io.ktor.server.application.install

fun Application.configureKoin() {
    install(Koin) {
        modules(gameModule)
    }
}