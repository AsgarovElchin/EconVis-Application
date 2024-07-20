package asgarov.elchin.econvis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class Country(
    @PrimaryKey val id: Long,
    val name: String,
    val region: String
)