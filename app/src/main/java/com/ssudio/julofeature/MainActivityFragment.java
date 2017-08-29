package com.ssudio.julofeature;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.ssudio.julofeature.address.model.Residence;
import com.ssudio.julofeature.contact.adapter.ContactAdapter;
import com.ssudio.julofeature.contact.model.Contact;
import com.ssudio.julofeature.contact.provider.JuloContactProvider;
import com.ssudio.julofeature.main.ui.IContactView;
import com.ssudio.julofeature.permission.PermissionUtility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivityFragment extends Fragment implements IContactView {

    @BindView(R.id.btnPhone1)
    protected Button btnPhone1;
    @BindView(R.id.btnPhone2)
    protected Button btnPhone2;
    @BindView(R.id.txtPhone1)
    protected EditText txtPhone1;
    @BindView(R.id.txtPhone2)
    protected EditText txtPhone2;
    @BindView(R.id.contactContainer)
    protected View contactContainer;

    @BindView(R.id.rvContacts)
    protected RecyclerView rvContacts;

    private Animation slideUpAnimation, slideDownAnimation;
    private JuloContactProvider contactProvider;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        slideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        contactProvider = new JuloContactProvider(getActivity().getContentResolver());

        View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, mainView);

        return mainView;
    }

    @OnClick(R.id.btnPhone1)
    protected void btnPhone1Clicked() {
        // getting request contact permission here, only show contact list if its permitted
        askForContactPermission();
    }

    @OnClick(R.id.btnPhone2)
    protected void btnPhone2Clicked() {
        dismiss();
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
                showContacts();
            }
        } else {
            showContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.JULO_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showContacts();
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

    private void showContacts() {
        final IContactView container = this;

        slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ArrayList<Contact> contacts = contactProvider.getPhoneContacts();

                ContactAdapter dataAdapter = new ContactAdapter(container, contacts);

                rvContacts.setHasFixedSize(true);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

                rvContacts.setLayoutManager(mLayoutManager);
                rvContacts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                rvContacts.setItemAnimator(new DefaultItemAnimator());
                rvContacts.setAdapter(dataAdapter);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contactContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        contactContainer.startAnimation(slideUpAnimation);
    }

    @Override
    public void select(Contact contact) {
        txtPhone1.setText(contact.getPhoneNumber());
        dismiss();
    }

    @Override
    public void dismiss() {
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contactContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        contactContainer.startAnimation(slideDownAnimation);
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
        }
    }
}
