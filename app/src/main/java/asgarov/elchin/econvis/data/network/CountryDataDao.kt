package asgarov.elchin.econvis.data.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.CountryData
import asgarov.elchin.econvis.data.model.GiniData
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.NewCountryData
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

    @Query("SELECT * FROM country_data WHERE countryId = :countryId AND indicator = :indicator AND year IN (:years)")
    suspend fun getCountryDataValues(countryId: Long, indicator: String, years: List<Int>): List<CountryData>

    @Query("SELECT DISTINCT countryId, countryName FROM country_data")
    suspend fun getAllCountries(): List<NewCountryData>

    @Query("DELETE FROM country_data WHERE countryId = :countryId")
    suspend fun deleteDataByCountry(countryId: Long)

    @Query("DELETE FROM country_data")
    suspend fun deleteAllData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiniData(giniData: List<GiniData>)

    @Query("SELECT * FROM gini_data WHERE year = :year")
    suspend fun getGiniDataByYear(year: Int): List<GiniData>


}