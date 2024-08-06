package asgarov.elchin.econvis.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase



val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `gini_data` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `iso` TEXT NOT NULL,
                `giniIndex` REAL NOT NULL,
                `year` INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}