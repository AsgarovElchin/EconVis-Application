package asgarov.elchin.econvis.data.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import asgarov.elchin.econvis.data.model.CountryData

@Dao
interface CountryDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(countryData: CountryData)

    @Query("SELECT * FROM country_data WHERE countryId = :countryId")
    suspend fun getCountryDataByCountryId(countryId: Long): List<CountryData>
}