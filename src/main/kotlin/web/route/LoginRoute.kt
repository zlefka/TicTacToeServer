package web.route

import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import web.model.LoginRequest

fun Route.loginRoute(userService: UserService) {
    post("/login") {
        val loginDto = call.receive<LoginRequest>()

        val auth = userService.authenticate(loginDto.login, loginDto.password)

        if ( auth == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "LogIn error"))
        } else {
            call.respond(HttpStatusCode.OK, auth.toString())
        }
    }
}