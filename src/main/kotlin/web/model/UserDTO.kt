package web.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO (
    val id: String,
    val login: String
)