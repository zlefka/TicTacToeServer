package web.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey


data class JwtProvider(private val secret: String,
                  private val audience: String,
                  private val issuer: String,
                  private val realm: String,
                  private val accessTokenValidity: Long,
                  private val refreshTokenValidity: Long) {

    private val signingKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateAccessToken(userId: UUID): String {
        val now = Date()
        return Jwts.builder()
            .issuer(issuer)
            .audience().add(audience).and()
            .issuedAt(now)
            .expiration(Date(now.time + accessTokenValidity))
            .claim("userId", userId.toString())
            .claim("type", "access")
            .signWith(signingKey)
            .compact()

    }


    fun generateRefreshToken(userId: UUID): String {
        val now = Date()

        return Jwts.builder()
            .issuer(issuer)
            .audience().add(audience).and()
            .issuedAt(now)
            .expiration(Date(now.time + refreshTokenValidity))
            .claim("userId", userId.toString())
            .claim("type", "refresh")
            .signWith(signingKey)
            .compact()
    }

    fun getClaims(token: String): Claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).payload // разбирает JWT (HEADER.PAYLOAD.SIGNATURE) и возвращает claims (данные токена)

    fun validateAccessToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            claims.get("type", String::class.java) == "access"
        } catch (e: Exception) {
            false
        }
    }

    fun validateRefreshToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            claims.get("type", String::class.java) == "refresh"
        } catch (e: Exception) {
            false
        }
    }

}