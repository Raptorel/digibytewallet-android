package io.digibyte.presenter.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.digibyte.R;
import io.digibyte.presenter.activities.adapters.AddressBookSpinnerAdapter;
import io.digibyte.presenter.activities.models.AddressBookViewModel;
import io.digibyte.presenter.activities.util.BRActivity;
import io.digibyte.tools.database.AddressBookDao;
import io.digibyte.tools.database.AddressBookEntity;
import io.digibyte.tools.database.Database;
import io.digibyte.tools.database.Resource;

public class AddressBookActivity extends BRActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.btn_save)
    Button saveBtn;
    @BindView(R.id.sw_editable)
    Switch editableSwitch;
    @BindView(R.id.sw_favorite)
    Switch favoriteSwitch;
    @BindView(R.id.et_name)
    EditText nameEditText;
    @BindView(R.id.et_address)
    EditText addressEditText;
    @BindView(R.id.spinner)
    Spinner addressesSpinner;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private AddressBookViewModel addressBookViewModel;
    private AddressBookSpinnerAdapter addressBookSpinnerAdapter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, AddressBookActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);
        ButterKnife.bind(this);

        setupViews();

        addressBookViewModel = ViewModelProviders.of(this).get(AddressBookViewModel.class);
        AddressBookDao addressBookDao = Database.instance.addressBookDao;
        addressBookViewModel.initRepository(addressBookDao);

        addressBookViewModel.getCorrespondences().observe(this, listResource -> {
            if (listResource.getState() == Resource.State.LOADING) {
                showDialog(mProgressBar);
            } else if (listResource.getState() == Resource.State.ERROR) {
                hideDialog(mProgressBar);
                Toast.makeText(getApplicationContext(), listResource.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (listResource.getState() == Resource.State.SUCCESS) {
                hideDialog(mProgressBar);
                List<AddressBookEntity> entities = listResource.getData();
                if (addressBookSpinnerAdapter == null) {
                    //deal with the initialization of the spinner, adapter and on item selected listener
                    addressBookSpinnerAdapter = new AddressBookSpinnerAdapter(this, android.R.layout.simple_spinner_item, entities);
                    addressBookSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    addressesSpinner.setAdapter(addressBookSpinnerAdapter);
                    addressesSpinner.setOnItemSelectedListener(this);
                } else addressBookSpinnerAdapter.updateData(entities);
            }
        });

        addressBookViewModel.getAddressBookEntries();
    }

    private void setupViews() {
        setupToolbar();
        setToolbarTitle(R.string.AddressBook_title);
    }

    @OnClick(R.id.btn_save)
    public void saveEntryInAddressBook() {
        String name = nameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        boolean isFavorite = favoriteSwitch.isChecked();

        addressBookViewModel.addNewAddressBookEntry(name, address, isFavorite);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //get the selected address book entity
        AddressBookEntity entity = addressBookSpinnerAdapter.getAddressBookEntities().get(position);
        nameEditText.setText(entity.getName());
        addressEditText.setText(entity.getAddress());
        favoriteSwitch.setChecked(entity.isFavorite());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
