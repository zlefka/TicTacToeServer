package web

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import web.module.configureAuthentication
import web.module.configureDatabases
import web.module.configureFeatures
import web.module.configureKoin
import web.module.configureSerialization
import web.route.configureRouting


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureFeatures()
    configureKoin()
    configureDatabases()
    configureAuthentication()
}