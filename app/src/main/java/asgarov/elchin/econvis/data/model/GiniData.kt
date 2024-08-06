package asgarov.elchin.econvis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gini_data")
data class GiniData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val iso: String,
    val giniIndex: Float,
    val year: Int
)