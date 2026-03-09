package web.security;

import domain.service.UserService
import java.util.UUID

class AuthService(val jwtProvider: JwtProvider, val userService: UserService) {
    fun login(request: JwtRequest): JwtResponse? {
        val auth = userService.authenticate(request.login, request.password)
        return if (auth != null) {
            JwtResponse(
                accessToken = jwtProvider.generateAccessToken(auth),
                refreshToken = jwtProvider.generateRefreshToken(auth)
            )
        } else null
    }

    fun refreshAccessToken(token: RefreshJwtRequest): JwtResponse? {

        if(!jwtProvider.validateRefreshToken(token.refreshToken)) return null

        val claims = jwtProvider.getClaims(token.refreshToken)

        val type = claims.get("type", String::class.java)
        if(type != "refresh") return null

        val userId = UUID.fromString(claims.get("userId", String::class.java))

        val user = userService.getUserById(userId) ?: return null

        return JwtResponse(
            accessToken = jwtProvider.generateAccessToken(user.id),
            refreshToken = token.refreshToken
        )
    }

    fun refreshRefToken(token: RefreshJwtRequest): JwtResponse? {
        if(jwtProvider.validateRefreshToken(token.refreshToken)) {
            val claims = jwtProvider.getClaims(token.refreshToken)
            val user = claims.get("userId", String::class.java)
            val id = UUID.fromString(user)
            return JwtResponse(
                accessToken = jwtProvider.generateAccessToken(id),
                refreshToken = jwtProvider.generateRefreshToken(id)
            )
        } else return null

    }
}