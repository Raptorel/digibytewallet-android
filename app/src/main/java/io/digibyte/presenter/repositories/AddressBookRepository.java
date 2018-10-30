package io.digibyte.presenter.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.digibyte.tools.database.AddressBookDao;
import io.digibyte.tools.database.AddressBookEntity;
import io.digibyte.tools.database.Resource;

public class AddressBookRepository {
    private final AddressBookDao addressBookDao;
    private Executor addressBookExecutor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private MutableLiveData<Resource<List<AddressBookEntity>>> addressBookListLiveData;

    public AddressBookRepository(AddressBookDao addressBookDao) {
        this.addressBookDao = addressBookDao;
        addressBookListLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Resource<List<AddressBookEntity>>> getAddressBookListLiveData() {
        return addressBookListLiveData;
    }

    public void addNewAddressBookEntry(final String name, final String address, final boolean isFavorite, DatabaseOperationsListener databaseOperationsListener) {
        addressBookExecutor.execute(() -> {
            AddressBookEntity addressBookEntity = new AddressBookEntity();
            addressBookEntity.setName(name);
            addressBookEntity.setAddress(address);
            addressBookEntity.setFavorite(isFavorite);

            //insert the newly created address book entry
            addressBookDao.insertAddressBookEntry(addressBookEntity);
            final List<AddressBookEntity> tempList = addressBookDao.getAll();

            //use the main thread to update the live data value of the list of entries
            mainHandler.post(() -> {
                addressBookListLiveData.setValue(Resource.success(tempList));
                databaseOperationsListener.onInsertedAddressBookEntry();
            });
        });
    }

    public void updateAddressBookEntry(final AddressBookEntity addressBookEntity, final String name,
                                       final String address, final boolean isFavorite, DatabaseOperationsListener databaseOperationsListener) {
        addressBookExecutor.execute(() -> {
            addressBookEntity.setName(name);
            addressBookEntity.setAddress(address);
            addressBookEntity.setFavorite(isFavorite);

            //update the entry in the address book
            addressBookDao.updateAddressBookEntry(addressBookEntity);

            final List<AddressBookEntity> tempList = addressBookDao.getAll();

            //use the main thread to update the live data value of the list of entries
            mainHandler.post(() -> {
                addressBookListLiveData.setValue(Resource.success(tempList));
                databaseOperationsListener.onUpdatedAddressBookEntry();
            });
        });
    }

    public void deleteAddressBookEntry(AddressBookEntity addressBookEntity, DatabaseOperationsListener databaseOperationsListener) {
        addressBookExecutor.execute(() -> {
            //delete the entry from the address book
            addressBookDao.deleteAddressBookEntry(addressBookEntity);

            final List<AddressBookEntity> tempList = addressBookDao.getAll();

            //use the main thread to update the live data value of the list of entries
            mainHandler.post(() -> {
                addressBookListLiveData.setValue(Resource.success(tempList));
                databaseOperationsListener.onDeletedAddressBookEntry();
            });
        });
    }

    public void getAddressBookEntries() {
        addressBookExecutor.execute(() -> {
            final List<AddressBookEntity> tempList = addressBookDao.getAll();

            //use the main thread to update the live data value of the list of entries
            mainHandler.post(() -> addressBookListLiveData.setValue(Resource.success(tempList)));
        });
    }

    public interface DatabaseOperationsListener{
        void onDeletedAddressBookEntry();
        void onUpdatedAddressBookEntry();
        void onInsertedAddressBookEntry();
    }
}
