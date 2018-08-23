package com.apps.codeit.getthere.activities;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.codeit.getthere.R;
import com.apps.codeit.getthere.constants.Constants;
import com.apps.codeit.getthere.models.GeoPlace;
import com.apps.codeit.getthere.models.Place;
import com.apps.codeit.getthere.services.AddressByNameIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainMap extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener{

    private long LOCATION_REFRESH_TIME = 1000;
    private float LOCATION_REFRESH_DISTANCE = 5;
    private final static String API_KEY = "AIzaSyBMmAZzi1ok9ZzDq3CDWChpoYYBFBQe7h8";

    private AutoCompleteTextView mainmap_search;

    GoogleMap mGoogleMap;
    private GoogleApiClient googleApiClient;

    public List<Address> addresses;
    ArrayAdapter<Address> adapter;
    public List<Place> places;
    public ArrayAdapter<Place> placeArrayAdapter;
    private BroadcastReceiver outputAddressReceiver;
    private AddressListResultReceiver addressResultReceiver;
    private LocationManager locationManager;

    FloatingActionButton mainmap_location, mainmap_send;

    List<Marker> markers;
    List<Polyline> lines;
    public List<GeoPlace> geoPlaces;
    public ArrayAdapter<GeoPlace> geoPlaceArrayAdapter;
    private LatLng myPos, myDest;
    Circle circle;
    private List<Circle> circles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        markers = new ArrayList<>();
        lines = new ArrayList<>();
        circles = new ArrayList<>();

        FirebaseApp.getInstance();

        requestPermission();

        if(googleServicesAvailable()){
            initMap();
        }

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /*
        if(addresses != null){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, addresses);
        }
        else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        }
        */
        addresses = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, addresses);
        places = new ArrayList<>();
        placeArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
        geoPlaces = new ArrayList<>();
        geoPlaceArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, geoPlaces);


        mainmap_search = findViewById(R.id.mainmap_search);
        //mainmap_search.setAdapter(adapter);
        //mainmap_search.setAdapter(placeArrayAdapter);
        mainmap_search.setAdapter(geoPlaceArrayAdapter);
        mainmap_search.setThreshold(1);
        mainmap_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getRawX() <= (mainmap_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()))
                {
                    // your action here
                    Toast.makeText(MainMap.this, "Clicked the left drawable", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        mainmap_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*
                    Intent intent = new Intent(MainMap.this, AddressByNameIntentService.class);
                    intent.putExtra("address_receiver", Parcels.wrap(addressResultReceiver));
                    intent.putExtra("address_name", mainmap_search.getText().toString().trim());
                    startService(intent);
                    */
                    //addressLookUp(mainmap_search.getText().toString().trim());
                    //geoPlacesLookUp(mainmap_search.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mainmap_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                geoPlacesLookUp(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getCurrentLocation();

        if (!Geocoder.isPresent()) {
            Toast.makeText(this,
                    "Can't find address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mainmap_location = findViewById(R.id.mainmap_location);
        mainmap_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCurrentLocation();
                    }
                });

        mainmap_send = findViewById(R.id.mainmap_send);
        mainmap_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawCircle(myPos, myDest);
            }
        });

        addressResultReceiver = new AddressListResultReceiver(new Handler());

    }

    private void drawCircle(final LatLng pos, final LatLng dest) {
        if(circles.size() > 0){
            for(Circle circle : circles){
                circle.remove();
                circles.remove(circle);
            }
        }
        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(pos);

        // Radius of the circle
        circleOptions.radius((CalculationByDistance(pos, dest) * 1000) / 2);

        // Border color of the circle
        circleOptions.strokeColor(0x3040C4FF);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(4);

        // Adding the circle to the GoogleMap
        //mGoogleMap.addCircle(circleOptions);
        circle = mGoogleMap.addCircle(circleOptions);
        circles.add(circle);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setIntValues(0, (int)((CalculationByDistance(pos, dest) * 1000) / 2));
        valueAnimator.setDuration(3000);
        valueAnimator.setEvaluator(new IntEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                //circle.setDimensions(animatedFraction * radius * 2);
                circle.setRadius(animatedFraction * (int)((CalculationByDistance(pos, dest) * 1000) / 2));
            }
        });

        valueAnimator.start();
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    private void getCurrentLocation() {
        //mGoogleMap.clear();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            myPos = new LatLng(location.getLatitude(), location.getLongitude());
            //moving the map to location
            moveMap(location.getLongitude(), location.getLatitude());
            locationMarker(location, "My Position");
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //moveMap(location.getLongitude(), location.getLatitude());
                        //locationMarker(location, "My Position");
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });

    }

    private void moveMap(double longitude, double latitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);


    }
    private void locationMarker(Location location, String title){
        if (markers.size() > 0){
            for(Marker marker : markers){
                if (marker.getTitle().equals(title)){
                    marker.remove();
                    markers.remove(marker);
                }
            }
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
        marker.setTitle(title);
        marker.setDraggable(false);
        markers.add(marker);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        if(markers.size() > 1){
            Log.d("MARKERS", String.valueOf(markers.size()));
            //drawRoute(getMarkerByTitle(markers, "My Position"), getMarkerByTitle(markers, "My Destination"));
        }


    }
    private Marker getMarkerByTitle(List<Marker> markers, String title){
        Marker marker = null;
        for(Marker marker1 : markers){
            if (marker1.getTitle().equals(title)){
                marker = marker1;
            }
        }
        return marker;
    }
    private void drawRoute(Marker pos, Marker dest){
        List<LatLng> path = new ArrayList<>();
        // Clear the polylines already drawn in the map
        if (lines.size() > 0){
            for (Polyline polyline : lines){
                polyline.remove();
                lines.remove(polyline);
            }
        }

        // Execute direction API context
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        DirectionsApiRequest request = DirectionsApi.getDirections(context, String.valueOf(pos.getPosition().latitude)+","+String.valueOf(pos.getPosition().longitude), String.valueOf(dest.getPosition().latitude)+","+String.valueOf(dest.getPosition().longitude));

        try{
            DirectionsResult result = request.await();

            // Loop through legs and steps to get encoded polylines of each step
            if (result.routes != null && result.routes.length > 0){
                DirectionsRoute route = result.routes[0];

                if (route.legs !=null){
                    for(int i=0; i<route.legs.length; i++){
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null){
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0){
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null){
                                            // Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1){
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                }
                                else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null){
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.getLocalizedMessage();
            Log.d("EXCEPTION", e.getMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            //Polyline polyline = mGoogleMap.addPolyline(opts);
            //lines.add(polyline);
            mGoogleMap.addPolyline(opts);
        }

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng position = new LatLng(pos.getPosition().latitude, pos.getPosition().longitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    private void geoPlacesLookUp(final String geoPlace){

        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://api.geonames.org/searchJSON?q="+geoPlace+"&username=bloworlf&style=full")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONArray jsonArray = new JSONObject(response.body().string()).getJSONArray("geonames");

                    geoPlaces.addAll(GeoPlace.fromJSONArray(jsonArray));
                    geoPlaceArrayAdapter.notifyDataSetChanged();
                }
                catch (JSONException | IOException e){
                    e.printStackTrace();
                }

                return null;
            }
        };
        task.execute(geoPlace);
    }

    public void addressLookUp(final String add) {
        if (places.size() > 0){
            places.clear();
            placeArrayAdapter.notifyDataSetChanged();
        }

        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        //.url("https://maps.googleapis.com/maps/api/geocode/json?address="+add+"&key="+MainMap.this.getResources().getString(R.string.api_key))
                        //.url("https://maps.googleapis.com/maps/api/geocode/json?address="+add+"&key="+API_KEY)
                        .url("https://maps.googleapis.com/maps/api/geocode/json?address="+add)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONArray jsonArray = new JSONObject(response.body().string()).getJSONArray("results");
                    //JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for(int i=0;i<jsonArray.length();i++){
                        Place place = Place.fromJSON(jsonArray.getJSONObject(i));
                        places.add(place);
                        placeArrayAdapter.notifyDataSetChanged();
                    }

                    //Place place = Place.fromJSON(jsonObject.getJSONObject( "results"));
                    //places.add(place);
                    //places.addAll(Place.fromJSONArray(jsonObject.getJSONArray("results")));
                    //placeArrayAdapter.notifyDataSetChanged();
                    Log.d("PLACES", places.toString());

                }
                catch (JSONException | IOException e){
                    e.printStackTrace();
                }

                return null;
            }
        };
        task.execute(add);
    }

    private class AddressListResultReceiver extends ResultReceiver {
        AddressListResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                Toast.makeText(MainMap.this,
                        "Enter address name, " ,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultCode == 1) {
                Toast.makeText(MainMap.this,
                        "Address not found, " ,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //String[] addressList = resultData.getStringArray("addressList");
            addresses = Parcels.unwrap(resultData.getParcelable("addressList"));
            Log.d("MAINMAPADDRESSES", addresses.toString());
            adapter.notifyDataSetChanged();

            //showResults(addressList);
        }
    }

    private boolean isRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        return false;
    }

    //Requesting permissions
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    //setContentView(R.layout.activity_main_map);

                }
                else {
                    // permission denied
                    Toast.makeText(this, "Permission denied to get your location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initMap() {
        MapFragment mapfragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapfragment.getMapAsync(this);
    }

    private boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if(api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }
        else
            Toast.makeText(this, "Cannot connect to Play Services", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(myPos != null){
            moveMap(myPos.latitude, myPos.longitude);
        }


        mGoogleMap.setOnMarkerDragListener(this);
        mGoogleMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        locationMarker(location, "My Destination");
        myDest = latLng;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //Toast.makeText(this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //Toast.makeText(this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

}
