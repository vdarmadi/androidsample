package com.ssudio.julofeature;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.ssudio.julofeature.address.model.Residence;
import com.ssudio.julofeature.contact.model.Contact;
import com.ssudio.julofeature.contact.provider.JuloContactProvider;
import com.ssudio.julofeature.permission.PermissionUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivityFragment extends Fragment {

    @BindView(R.id.etPhoneNumberOne)
    protected EditText etPhoneNumberOne;
    @BindView(R.id.etPhoneNumberTwo)
    protected EditText etPhoneNumberTwo;

    private JuloContactProvider contactProvider;
    private boolean permittedToReadContact = false;

    private static final int GET_CONTACT_FROM_ADDRESS_BOOK_FOR_CONTACT1 = 1111;
    private static final int GET_CONTACT_FROM_ADDRESS_BOOK_FOR_CONTACT2 = 1112;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contactProvider = new JuloContactProvider(getActivity().getContentResolver());

        View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, mainView);

        // getting request contact permission here, only show contact list if its permitted
        askForContactPermission();

        setupEditTextContactListener();

        return mainView;
    }

    private void setupEditTextContactListener() {
        etPhoneNumberOne.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return editTextTouchHandler(v, event, true);
            }
        });

        etPhoneNumberTwo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return editTextTouchHandler(v, event, false);
            }
        });
    }

    private boolean editTextTouchHandler(View v, MotionEvent event, boolean isContactOne) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int drawableRightBoundWidth = ((EditText)v)
                    .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();

            if (event.getRawX() >= (v.getRight() - drawableRightBoundWidth)) {
                if (isContactOne) {
                    showContactBookForContactOne();
                } else {
                    showContactBookForContactTwo();
                }

                return true;
            }
        }

        return false;
    }

    protected void showContactBookForContactOne() {
        if (permittedToReadContact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

            startActivityForResult(intent, GET_CONTACT_FROM_ADDRESS_BOOK_FOR_CONTACT1);
        }
    }

    protected void showContactBookForContactTwo() {
        if (permittedToReadContact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

            startActivityForResult(intent, GET_CONTACT_FROM_ADDRESS_BOOK_FOR_CONTACT2);
        }
    }

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Allow this app to read your contacts?");

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    ,PermissionUtility.JULO_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });

                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PermissionUtility.JULO_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                permittedToReadContact = true;
            }
        } else {
            permittedToReadContact = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.JULO_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*showContactsIntent();*/

                    permittedToReadContact = true;

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @OnClick(R.id.btnShowMap)
    protected void btnShowMapClicked() {
        startActivityForResult(new Intent(getActivity(), MyAddressActivity.class),
                MyAddressActivity.GET_ADDRESS_FROM_POSITION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MyAddressActivity.GET_ADDRESS_FROM_POSITION) {
            if (resultCode == Activity.RESULT_OK) {
                Residence residence = new Gson()
                        .fromJson(data.getStringExtra("userResidence"), Residence.class);

                Log.d("JULO", "Main address:" + residence.getStreetName());
            }
        } else if (requestCode == GET_CONTACT_FROM_ADDRESS_BOOK_FOR_CONTACT1) {
            if (resultCode == Activity.RESULT_OK) {
                Contact contact = contactProvider.getContactFromUri(data.getData());

                etPhoneNumberOne.setText(contact.getPhoneNumber());
            }
        } else if (requestCode == GET_CONTACT_FROM_ADDRESS_BOOK_FOR_CONTACT2) {
            if (resultCode == Activity.RESULT_OK) {
                Contact contact = contactProvider.getContactFromUri(data.getData());

                etPhoneNumberTwo.setText(contact.getPhoneNumber());
            }
        }
    }
}
