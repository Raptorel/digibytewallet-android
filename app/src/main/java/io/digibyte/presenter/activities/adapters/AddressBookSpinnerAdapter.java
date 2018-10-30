package io.digibyte.presenter.activities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

import io.digibyte.tools.database.AddressBookEntity;

public class AddressBookSpinnerAdapter extends ArrayAdapter {

    public static final String SPINNER_HEADER = "Add a new entry...";

    List<AddressBookEntity> addressBookEntities;

    public AddressBookSpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public void updateData(final List<AddressBookEntity> data) {
        addressBookEntities = data;
        //add the spinner header
        addressBookEntities.add(0, addNewEntryPlaceholder());
        //add the actual address book entries
        addAll(addressBookEntities = data);
        notifyDataSetChanged();
    }

    public List<AddressBookEntity> getAddressBookEntities() {
        return addressBookEntities;
    }

    private AddressBookEntity addNewEntryPlaceholder() {
        AddressBookEntity entity = new AddressBookEntity();
        entity.setName(SPINNER_HEADER);
        entity.setAddress("");
        entity.setFavorite(false);
        return entity;
    }

    public interface OnSpinnerDataChangedListener{
        void onSpinnerAdapterDataChanged();
    }
}
