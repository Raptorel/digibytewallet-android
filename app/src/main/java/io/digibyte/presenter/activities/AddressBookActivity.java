package io.digibyte.presenter.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.digibyte.R;
import io.digibyte.presenter.activities.util.BRActivity;

public class AddressBookActivity extends BRActivity {

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

    public static void show(Context context) {
        context.startActivity(new Intent(context, AddressBookActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);
        ButterKnife.bind(this);
        setupViews();
    }

    private void setupViews() {
        setupToolbar();
        setToolbarTitle(R.string.AddressBook_title);
    }
}
