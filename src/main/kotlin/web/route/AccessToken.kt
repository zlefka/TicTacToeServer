package web.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import web.security.AuthService
import web.security.RefreshJwtRequest

fun Route.refreshAccessToken(authService: AuthService) {
    post("/auth/refresh/access") {
        val request = call.receive<RefreshJwtRequest>()

        val auth = authService.refreshAccessToken(request)

        if ( auth == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token error"))
        } else {
            call.respond(HttpStatusCode.OK, auth)
        }
    }
}