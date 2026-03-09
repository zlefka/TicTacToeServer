package web.module

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.jwt



fun Application.configureJWT() {
    val jwtConfig = environment.config.config("jwt")

    val secret = jwtConfig.property("secret").getString()
    val audience = jwtConfig.property("audience").getString()
    val issuer = jwtConfig.property("issuer").getString()
    val realm = jwtConfig.property("realm").getString()

    install(Authentication) {
        jwt("jwt-auth") {
            this.realm = realm

            val algorithm = Algorithm.HMAC256(secret)

            val jwtVerifier = JWT.require(algorithm).withIssuer(issuer).withAudience(audience).build() // проверяет данные
            verifier(jwtVerifier)

            validate { credential -> // вызывается если токен валиден
                val userId = credential.payload.getClaim("userId").asString()

                if(userId != null) {
                    UserIdPrincipal(userId)
                } else null
            }
        }
    }
}