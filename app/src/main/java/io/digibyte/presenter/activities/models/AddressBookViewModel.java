package io.digibyte.presenter.activities.models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.digibyte.presenter.repositories.AddressBookRepository;
import io.digibyte.tools.database.AddressBookDao;
import io.digibyte.tools.database.AddressBookEntity;
import io.digibyte.tools.database.Resource;

public class AddressBookViewModel extends ViewModel {

    private AddressBookRepository addressBookRepository;
    private AddressBookRepository.DatabaseOperationsListener databaseOperationsListener;

    public void initRepository(final AddressBookDao addressBookDao, AddressBookRepository.DatabaseOperationsListener listener) {
        addressBookRepository = new AddressBookRepository(addressBookDao);
        databaseOperationsListener = listener;
    }

    public LiveData<Resource<List<AddressBookEntity>>> getCorrespondences() {
        return addressBookRepository.getAddressBookListLiveData();
    }

    public void addNewAddressBookEntry(String name, String address, boolean isFavorite) {
        addressBookRepository.addNewAddressBookEntry(name, address, isFavorite, databaseOperationsListener);
    }

    public void getAddressBookEntries() {
        addressBookRepository.getAddressBookEntries();
    }

    public void updateSpecificAddressBookEntry(AddressBookEntity addressBookEntity, String name, String address, boolean isFavorite){
        addressBookRepository.updateAddressBookEntry(addressBookEntity, name, address, isFavorite, databaseOperationsListener);
    }

    public void deleteSpecificAddressBookEntry(AddressBookEntity entity) {
        addressBookRepository.deleteAddressBookEntry(entity, databaseOperationsListener);
    }
}
