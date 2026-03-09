package web.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import web.security.AuthService
import web.security.RefreshJwtRequest

fun Route.refreshToken(authService: AuthService) {
    post("/auth/refresh") {
        val request = call.receive<RefreshJwtRequest>()

        val auth = authService.refreshRefToken(request)

        if ( auth == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token error"))
        } else {
            call.respond(HttpStatusCode.OK, auth)
        }
    }
}