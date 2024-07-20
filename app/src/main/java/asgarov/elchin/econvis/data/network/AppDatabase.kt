package asgarov.elchin.econvis.data.network

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import asgarov.elchin.econvis.data.MIGRATION_2_3
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.CountryData
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Year

@Database(entities = [Country::class, Indicator::class, Year::class, CountryData::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDataDao(): CountryDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "econvis_database"
                )
                    .addMigrations(MIGRATION_2_3) // Add the migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}