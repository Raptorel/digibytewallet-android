package io.digibyte.tools.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class Migrations {
    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `addressBook` (" +
                    "`uid` INTEGER NOT NULL, " +
                    "`name` TEXT," +
                    "`address` TEXT," +
                    "`isFavorite` INTEGER DEFAULT 0 NOT NULL," +
                    "PRIMARY KEY(`uid`)" + ")");
        }
    };
}
