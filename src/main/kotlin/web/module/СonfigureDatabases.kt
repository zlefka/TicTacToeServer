package web.module

import datasource.database.GameTable
import datasource.database.Users
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID


fun Application.configureDatabases() {
    val jdbcUrl = environment.config.property("storage.jdbcURL").getString()
    val user = environment.config.property("storage.user").getString()
    val password = environment.config.property("storage.password").getString()
    val driver = environment.config.property("storage.driverClassName").getString()

    log.info("Connecting to: $jdbcUrl")

    try {
        Database.connect(
            url = jdbcUrl,
            driver = driver,
            user= user,
            password = password
        )

        transaction {
            SchemaUtils.create(Users, GameTable)
            log.info("Database tables created successfully.")

            val botLogin = "computer"
            val existingBot = Users.select (Users.login).where(Users.login eq botLogin ).firstOrNull()
            if (existingBot == null) {
                Users.insert {
                    it[id] = UUID.randomUUID()
                    it[login] = botLogin
                    it[passwordHash] = hashPasswordForInit(UUID.randomUUID().toString())
                }
                log.info("Bot created")
            } else {
                log.info("Bot already exists")
            }
        }
    } catch (_: Exception) {
        log.error("Error while connecting to database")
    }
}

fun hashPasswordForInit(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val hashed = md.digest(password.toByteArray())
    return Base64.getEncoder().encodeToString(hashed)
}