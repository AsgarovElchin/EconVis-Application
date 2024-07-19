package asgarov.elchin.econvis.data



import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Define the migration strategy from version 1 to version 2.
        // For example, if you are adding a new column:
        database.execSQL("ALTER TABLE country_data ADD COLUMN new_column_name TEXT")
        // Add any other necessary migration steps here
    }
}
