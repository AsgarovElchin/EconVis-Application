package asgarov.elchin.econvis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "country_data")
data class CountryData(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val countryId: Long,
    val countryName: String?,
    val indicator: String,
    val year: Int,
    val value: Double
)