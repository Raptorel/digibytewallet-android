package io.digibyte.tools.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AddressBookDao {
    @Query("SELECT * FROM addressBook")
    List<AddressBookEntity> getAll();

    @Query("SELECT * FROM addressBook WHERE uid = :id")
    public AddressBookEntity getSpecificAddressBook(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertAddressBookEntry(AddressBookEntity entity);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    public void updateAddressBookEntry(AddressBookEntity entity);

    @Delete
    public void deleteAddressBookEntry(AddressBookEntity entity);
}
