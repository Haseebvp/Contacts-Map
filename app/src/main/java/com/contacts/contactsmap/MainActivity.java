package com.contacts.contactsmap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.Pager;
import de.greenrobot.event.EventBus;
import models.MessageEvent;
import models.UpdateMapEvent;
import viewAddons.CustomMessage;
import constants.UrlParams;
import models.ContactModel;
import network.ApiCommunication;
import network.ApiService;
import network.JsonParser;


public class MainActivity extends AppCompatActivity implements ApiCommunication, LoaderManager.LoaderCallbacks<ArrayList<ContactModel>>, TabLayout.OnTabSelectedListener {
    String SCREEN_NAME = "MAINACTIVITY";

    LinearLayout lv_main;
    TabLayout tb_tabLayout;
    ViewPager vp_pager;
    ArrayList<ContactModel> data = new ArrayList<ContactModel>();
    Pager adapter;
    EventBus myEventBus = EventBus.getDefault();
    AVLoadingIndicatorView loader;
    TextView loaderText;
    boolean doubleBackToExitPressedOnce = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);


        lv_main = (LinearLayout) findViewById(R.id.main_layout);
        tb_tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        vp_pager = (ViewPager) findViewById(R.id.pager);
        lv_main.setVisibility(View.INVISIBLE);
        loader = (AVLoadingIndicatorView) findViewById(R.id.loader);
        loaderText = (TextView) findViewById(R.id.loaderText);

        Loader(true, "Fetching Data ...");

        getSupportLoaderManager().initLoader(1, null, this);
        GetData();

        tb_tabLayout.setTabTextColors(Color.parseColor("#BDBDBD"), Color.BLACK);
        tb_tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tb_tabLayout.setOnTabSelectedListener(this);
        tb_tabLayout.setupWithViewPager(vp_pager);
    }


    private void GetData() {
        ApiService.getInstance(MainActivity.this, 1).getData(MainActivity.this, true, SCREEN_NAME, UrlParams.BaseUrl + "/contacts", "getContacts");
    }

    ;

    @Override
    public void onResponseCallback(JSONArray response, String flag) {
        if (flag.equals("getContacts")) {
            data = JsonParser.getInstance().parseResponse(response);
            if (data.size() == 0) {
                Loader(true, "Error from server. Syncing local contacts ...");
            } else {
                Loader(true, "Syncing local contacts ...");
            }
        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {
        loader.hide();
        CustomMessage.getInstance().CustomMessage(this, "Oops. Something went wrong!");
    }

    public void selectFragment(int position){
        vp_pager.setCurrentItem(position, true);

// true is to animate the transaction
    }

    public void updateMap(int position){
        myEventBus.post(new UpdateMapEvent(position));
// true is to animate the transaction
    }


    @Override
    public Loader<ArrayList<ContactModel>> onCreateLoader(int id, Bundle args) {
        return new FetchData(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ContactModel>> loader, ArrayList<ContactModel>data1) {
        System.out.println("DATA loader finished: " + data1);
        data.addAll(data1);
        Loader(false, "Fetching Data ...");
        adapter = new Pager(getSupportFragmentManager(), tb_tabLayout.getTabCount(), data);
        vp_pager.setAdapter(adapter);
        tb_tabLayout.setupWithViewPager(vp_pager);
        lv_main.setVisibility(View.VISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ContactModel>> loader) {

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        vp_pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private static class FetchData extends AsyncTaskLoader<ArrayList<ContactModel>> {

        public FetchData(Context context) {
            super(context);
        }


        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            forceLoad();
        }

        @Override
        public ArrayList<ContactModel> loadInBackground() {
            ArrayList<ContactModel> phoneData = new ArrayList<ContactModel>();
            ContentResolver cr = getContext().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {

                while (cur.moveToNext()) {
                    String phoneNo = null;
                    String email = null;
                    String address = null;
                    String name = null;
                    Double latitude = null;
                    Double longitude = null;
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (pCur.moveToFirst()) {
                            phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        pCur.close();
                        Cursor cur1 = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (cur1.moveToFirst()) {
                            email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        }
                        cur1.close();
                        Cursor cur2 = cr.query(
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (cur2.getCount() > 0) {
                            cur2.moveToFirst();
                            address = cur2.getString(cur2.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                            Geocoder coder = new Geocoder(getContext());
                            List<Address> addressList = null;
                            try {
                                addressList = coder.getFromLocationName(address, 5);
                                if (addressList.size() > 0) {
                                    Address location = addressList.get(0);
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (address == null) {
                                return null;
                            }

                        } else {
                            address = null;
                            latitude = null;
                            longitude = null;
                        }
                        cur2.close();
                    }
                    if (phoneNo != null) {
                        ContactModel contactmodel = new ContactModel();
                        contactmodel.setName(name);
                        contactmodel.setPhone(phoneNo);
                        contactmodel.setEmail(email);
                        contactmodel.setLatitude(latitude);
                        contactmodel.setLongitude(longitude);
                        phoneData.add(contactmodel);
                    }
                }
            }
            return phoneData;
        }

        @Override
        public void deliverResult(ArrayList<ContactModel> data) {
            System.out.println("DATA deliver result: " + data);
            super.deliverResult(data);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        myEventBus.unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myEventBus.register(this);
    }
    public void onEvent(MessageEvent event){
//        Toast.makeText(MainActivity.this, event.getData().toString(), Toast.LENGTH_SHORT).show();
    }


    public void Loader(final Boolean status, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status){
                    loaderText.setText(message);
                    loader.show();
                }
                else {
                    loader.hide();
                    loaderText.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        CustomMessage.getInstance().CustomMessage(this, "Press again to close the app");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
