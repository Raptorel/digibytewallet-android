package io.digibyte.presenter.activities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

import io.digibyte.tools.database.AddressBookEntity;

public class AddressBookSpinnerAdapter extends ArrayAdapter {

    public AddressBookSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<AddressBookEntity> entities) {
        super(context, resource, entities);
    }

    public void updateData(final List<AddressBookEntity> data) {
        if (getCount() > 0) clear();
        addAll(data);
        notifyDataSetChanged();
    }
}
