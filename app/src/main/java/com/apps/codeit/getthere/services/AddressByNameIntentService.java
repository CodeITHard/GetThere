package com.apps.codeit.getthere.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressByNameIntentService extends IntentService {

    private ResultReceiver addressResultReceiver;

    public AddressByNameIntentService(){
        super("AddressByName");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String msg = "";
        addressResultReceiver = Parcels.unwrap(intent.getParcelableExtra("address_receiver"));

        if (addressResultReceiver == null) {
            Log.e("AddressByName",
                    "No receiver in intent");
            return;
        }

        String addressName = intent.getStringExtra("address_name");

        if (addressName == null) {
            msg = "No name found";
            sendResultsToReceiver(0, msg, null);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocationName(addressName, 5);
            Log.d("ADRESSES", addresses.toString());
        } catch (Exception ioException) {
            Log.e("", "Error in getting addresses for the given name");
        }

        if (addresses == null || addresses.size()  == 0) {
            msg = "No address found for the address name";
            sendResultsToReceiver(1, msg, null);
        }
        else {
            Log.d("AddressByName", "number of addresses received "+addresses.size());
            /*
            String[] addressList = new String[addresses.size()] ;
            int j =0;
            for(Address address : addresses){
                ArrayList<String> addressInfo = new ArrayList<>();
                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressInfo.add(address.getAddressLine(i));
                }
                addressList[j] = TextUtils.join(System.getProperty("line.separator"),
                        addressInfo);
                Log.d("AddressByName",addressList[j]);
                j++;
            }
            */
            Log.d("ADDRESSES", addresses.toString());
            sendResultsToReceiver(2,
                    "",
                    //addressList);
                    Parcels.wrap(addresses));
        }
    }

    private void sendResultsToReceiver(int resultCode, String message, Parcelable addressList) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", message);
        bundle.putParcelable("addressList", addressList);
        addressResultReceiver.send(resultCode, bundle);
    }
}
