package web.module

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureFeatures() {
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            !call.request.path().startsWith("/health")
        }
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "Internal Server Error: ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Not Found", status = status)
        }
    }
}