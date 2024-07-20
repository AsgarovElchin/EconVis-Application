package asgarov.elchin.econvis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "indicators")
data class Indicator(
    @PrimaryKey val id: Long,
    val name: String
)
