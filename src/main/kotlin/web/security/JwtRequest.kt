package web.security

import kotlinx.serialization.Serializable

@Serializable
data class JwtRequest (
    val login: String,
    val password: String
    )

@Serializable
data class JwtResponse(
    val type: String = "Bearer",
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class RefreshJwtRequest(
    val refreshToken: String
)