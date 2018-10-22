package io.digibyte.presenter.repositories;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.digibyte.tools.database.AddressBookDao;
import io.digibyte.tools.database.AddressBookEntity;
import io.digibyte.tools.database.Resource;

public class AddressBookRepository {
    private final AddressBookDao addressBookDao;
    private Executor addressBookExecutor = Executors.newSingleThreadExecutor();

    private MutableLiveData<Resource<List<AddressBookEntity>>> addressBookListLiveData;
    private MutableLiveData<Resource<AddressBookEntity>> addressBookEntryLiveData;

    public AddressBookRepository(AddressBookDao addressBookDao) {
        this.addressBookDao = addressBookDao;
        addressBookListLiveData = new MutableLiveData<Resource<List<AddressBookEntity>>>();
        addressBookEntryLiveData = new MutableLiveData<Resource<AddressBookEntity>>();
    }

    public MutableLiveData<Resource<List<AddressBookEntity>>> getAddressBookListLiveData() {
        return addressBookListLiveData;
    }

    public MutableLiveData<Resource<AddressBookEntity>> getAddressBookEntryLiveData() {
        return addressBookEntryLiveData;
    }

    public void addNewAddressBookEntry(final String name, final String address, final boolean isFavorite) {
        addressBookExecutor.execute(() -> {
            AddressBookEntity addressBookEntity = new AddressBookEntity();
            addressBookEntity.setName(name);
            addressBookEntity.setAddress(address);
            addressBookEntity.setFavorite(isFavorite);

            addressBookDao.insertAddressBookEntry(addressBookEntity);
        });
    }

    public void updateAddressBookEntry(final AddressBookEntity addressBookEntity, final String name,
                                       final String address, final boolean isFavorite) {
        addressBookExecutor.execute(() -> {
            addressBookEntity.setName(name);
            addressBookEntity.setAddress(address);
            addressBookEntity.setFavorite(isFavorite);

            addressBookDao.updateAddressBookEntry(addressBookEntity);
        });
    }

    public void deleteAddressBookEntry(AddressBookEntity addressBookEntity) {
        addressBookExecutor.execute(() -> {
            addressBookDao.deleteAddressBookEntry(addressBookEntity);
        });
    }
}
