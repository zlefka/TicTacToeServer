package web.route

import domain.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.util.UUID

fun Route.getUserInfo(userService: UserService) {
    get("/user/{id}") {
        val principal = call.principal<UserIdPrincipal>()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)

        val requestingUserId = UUID.fromString(principal.name)

        val userId = call.parameters["id"]?.let { UUID.fromString(it) }
            ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user id"))

        val user = userService.getUserById(userId)
            ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))

        call.respond(mapOf(
            "id" to user.id.toString(),
            "login" to user.login
        ))
    }
}