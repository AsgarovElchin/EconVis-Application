package asgarov.elchin.econvis.data.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.CountryData
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Year

@Dao
interface CountryDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<Country>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIndicators(indicators: List<Indicator>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYears(years: List<Year>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(countryData: CountryData)

    @Query("SELECT * FROM countries")
    suspend fun getCountries(): List<Country>

    @Query("SELECT * FROM indicators")
    suspend fun getIndicators(): List<Indicator>

    @Query("SELECT * FROM years")
    suspend fun getYears(): List<Year>

    @Query("SELECT value FROM country_data WHERE countryId = :countryId AND indicator = :indicator AND year = :year")
    suspend fun getCountryDataValue(countryId: Long, indicator: String, year: Int): Double?

?
}