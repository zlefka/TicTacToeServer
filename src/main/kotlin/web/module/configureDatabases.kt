package web.module

import datasource.database.GameTable
import datasource.database.Users
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureDatabases() {
    val jdbcUrl = environment.config.property("storage.jdbcURL").getString()
    val user = environment.config.property("storage.user").getString()
    val password = environment.config.property("storage.password").getString()
    val driver = environment.config.property("storage.driverClassName").getString()

    log.info("Подключение к базе данных по URL: $jdbcUrl")

    try {
        Database.connect(
            url = jdbcUrl,
            driver = driver,
            user=user,
            password = password
        )

        transaction {

            SchemaUtils.create(Users, GameTable)
            log.info("Таблицы базы данных проверены/созданы.")
        }
    } catch (_: Exception) {
        log.error("Ошибка подключения к БД")
    }
}