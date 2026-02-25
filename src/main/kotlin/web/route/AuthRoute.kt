package web.route

import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import web.model.SignUpRequest

fun Route.signUpRoute(userService: UserService) {
    post("/signup") {
        try {
            val signupDto = call.receive<SignUpRequest>()

            if (!userService.register(signupDto.login, signupDto.password)) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "User is already exist"))
            } else {
                call.respond(HttpStatusCode.Created, "User created")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
        }
    }
}