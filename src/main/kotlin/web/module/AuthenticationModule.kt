package web.module

import domain.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import org.koin.ktor.ext.inject

fun Application.configureAuthentication() {
    val userService by inject<UserService>()
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                val auth = userService.authenticate(credentials.name, credentials.password)
                if(auth != null)
                    UserIdPrincipal(auth.toString())
                else null
            }
        }
    }
}