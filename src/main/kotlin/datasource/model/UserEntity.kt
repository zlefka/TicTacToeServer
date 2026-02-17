package datasource.model

import java.util.UUID

data class UserEntity(
    val id: UUID,
    val login: String,
    val passwordHash: String
)