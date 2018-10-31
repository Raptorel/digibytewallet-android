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
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.digibyte.R;
import io.digibyte.presenter.activities.adapters.AddressBookSpinnerAdapter;
import io.digibyte.presenter.activities.models.AddressBookViewModel;
import io.digibyte.presenter.activities.util.BRActivity;
import io.digibyte.presenter.repositories.AddressBookRepository;
import io.digibyte.tools.database.AddressBookDao;
import io.digibyte.tools.database.AddressBookEntity;
import io.digibyte.tools.database.Database;
import io.digibyte.tools.database.Resource;

public class AddressBookActivity extends BRActivity
        implements AddressBookSpinnerAdapter.OnSpinnerDataChangedListener, AddressBookRepository.DatabaseOperationsListener,
        AdapterView.OnItemSelectedListener {

    @BindView(R.id.btn_save)
    Button saveBtn;
    @BindView(R.id.btn_delete)
    Button deleteBtn;
    @BindView(R.id.btn_use)
    Button useBtn;
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
    private boolean newEntry;
    private AddressBookEntity currentEntity;
    private static final String EMPTY_STRING = "";
    public static final String USE_ADDRESS_FOR_SEND_OR_RECEIVE_KEY = "UseAddressForSendOrReceive";
    public static final String ADDRESS_EXTRA = "AddressExtra";
    public static final int USE_ADDRESS_FOR_SEND_OR_RECEIVE_CODE = 12345;
    public static final int RESULT_OK = 99999;
    private boolean fromSendOrReceive;

    public static void show(Context context) {
        context.startActivity(new Intent(context, AddressBookActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);
        ButterKnife.bind(this);

        //determine if this comes from the send or receive fragments and if so, display the "Use" button
        fromSendOrReceive = getIntent().getBooleanExtra(USE_ADDRESS_FOR_SEND_OR_RECEIVE_KEY, false);

        setupViews();

        addressBookViewModel = ViewModelProviders.of(this).get(AddressBookViewModel.class);
        AddressBookDao addressBookDao = Database.instance.addressBookDao;
        addressBookViewModel.initRepository(addressBookDao, this);

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
                    addressBookSpinnerAdapter = new AddressBookSpinnerAdapter(this, android.R.layout.simple_spinner_item);
                    addressBookSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    addressesSpinner.setAdapter(addressBookSpinnerAdapter);
                    addressesSpinner.setOnItemSelectedListener(this);
                }
                addressBookSpinnerAdapter.clear();
                addressBookSpinnerAdapter.updateData(entities);
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
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        boolean isFavorite = favoriteSwitch.isChecked();

//        //check for address validity
//        if (!address.startsWith("d") || !(address.length()==34)) {
//            Toast.makeText(this, "That is not a valid Digibyte address", Toast.LENGTH_SHORT).show();
//            return;
//        }

        //if it's a new entry, insert it
        if (newEntry) {
            //don't add an entry which contains the header of the spinner
            if (!name.contains(AddressBookSpinnerAdapter.SPINNER_HEADER)) {
                addressBookViewModel.addNewAddressBookEntry(name, address, isFavorite);
            } else {
                Toast.makeText(this, "You cannot add an entry with that name", Toast.LENGTH_SHORT).show();
            }
        //else, update the already existing entity
        } else {
            addressBookViewModel.updateSpecificAddressBookEntry(currentEntity, name, address, isFavorite);
        }
    }

    @OnClick(R.id.btn_delete)
    public void deleteEntryInAddressBook() {
        addressBookViewModel.deleteSpecificAddressBookEntry(currentEntity);
    }

    @OnClick(R.id.btn_use)
    public void useCurrentAddress() {
        //get the address of the selected entity
        String address = addressEditText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(ADDRESS_EXTRA, address);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnCheckedChanged(R.id.sw_editable)
    public void toggleEditableEditTexts() {
        if (editableSwitch.isChecked()) {
            nameEditText.setEnabled(true);
            addressEditText.setEnabled(true);
            favoriteSwitch.setEnabled(true);
            saveBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
        } else {
            nameEditText.setEnabled(false);
            addressEditText.setEnabled(false);
            favoriteSwitch.setEnabled(false);
            saveBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //get the selected address book entity
        currentEntity = addressBookSpinnerAdapter.getAddressBookEntities().get(position);
        populateFields();
    }

    private void populateFields() {
        //don't set the name if it's the header
        if (!currentEntity.getName().equals(AddressBookSpinnerAdapter.SPINNER_HEADER)) {
            nameEditText.setText(currentEntity.getName());
            addressEditText.setText(currentEntity.getAddress());
            editableSwitch.setChecked(false);
            editableSwitch.setEnabled(true);
            deleteBtn.setVisibility(View.VISIBLE);
            favoriteSwitch.setChecked(currentEntity.isFavorite());
            newEntry = false;
            if(fromSendOrReceive) useBtn.setVisibility(View.VISIBLE); else useBtn.setVisibility(View.GONE);
        } else {
            clearFields();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDeletedAddressBookEntry() {
        Toast.makeText(this, "Address successfully deleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdatedAddressBookEntry() {
        Toast.makeText(this, "Address successfully updated!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInsertedAddressBookEntry() {
        Toast.makeText(this, "Address successfully added!", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields(){
        nameEditText.setText(EMPTY_STRING);
        addressEditText.setText(EMPTY_STRING);
        editableSwitch.setChecked(true);
        editableSwitch.setEnabled(false);
        deleteBtn.setVisibility(View.GONE);
        favoriteSwitch.setChecked(false);
        useBtn.setVisibility(View.GONE);
        newEntry = true;
    }

    @Override
    public void onSpinnerAdapterDataChanged() {
        //get the selected address book entity
        currentEntity = addressBookSpinnerAdapter.getAddressBookEntities().get(addressesSpinner.getSelectedItemPosition());
        populateFields();
    }
}
