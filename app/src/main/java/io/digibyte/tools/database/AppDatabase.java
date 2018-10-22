package io.digibyte.tools.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {DigiTransaction.class, AddressBookEntity.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();

    public abstract AddressBookDao addressBookDao();
}