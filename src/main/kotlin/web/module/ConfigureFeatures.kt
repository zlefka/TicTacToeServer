package web.module

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureFeatures() {

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to cause.message))
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Not Found", status = status)
        }
    }
}