package com.apps.codeit.getthere.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.apps.codeit.getthere.constants.Constants;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;
    Geocoder geocoder;

    public FetchAddressIntentService(){
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        geocoder = new Geocoder(this);

        if (intent == null) {
            return;
        }
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        //Location location = intent.getParcelableExtra(
        //        Constants.LOCATION_DATA_EXTRA);

        // Get the location name passed to this service thtrough an extra
        String location = intent.getStringExtra("location");

        List<Address> addresses = null;

        try {
            //addresses = geocoder.getFromLocation(
            //        location.getLatitude(),
            //        location.getLongitude(),
            //        1);
            addresses = geocoder.getFromLocationName(
                    location,
                    5
            );
        } catch (IOException | IllegalArgumentException ioException) {
            // Catch network or other I/O problems.
            //errorMessage = getString(R.string.service_not_available);
            //Log.e(TAG, errorMessage, ioException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                //errorMessage = getString(R.string.no_address_found);
                //Log.e(TAG, errorMessage);
            }
            //deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            /*
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            //Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
            */
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addresses);
        }


    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
    private void deliverResultToReceiver(int resultCode, List<Address> addresses) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_DATA_KEY, Parcels.wrap(addresses));
        mReceiver.send(resultCode, bundle);
    }
}
