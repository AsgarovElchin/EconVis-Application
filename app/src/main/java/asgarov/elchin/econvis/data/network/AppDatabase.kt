package asgarov.elchin.econvis.data.network

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import asgarov.elchin.econvis.data.MIGRATION_1_2
import asgarov.elchin.econvis.data.model.CountryData

@Database(entities = [CountryData::class], version = 2)
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
                    .addMigrations(MIGRATION_1_2) // Add the migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}