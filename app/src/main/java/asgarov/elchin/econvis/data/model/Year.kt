package asgarov.elchin.econvis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "years")
data class Year(
    @PrimaryKey val id: Long,
    val year: Int
)