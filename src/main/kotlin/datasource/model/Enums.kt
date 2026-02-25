package datasource.model

import kotlinx.serialization.Serializable

@Serializable
enum class CellEntity {
    EMPTY,
    X,
    O
}