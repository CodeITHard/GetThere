package com.apps.codeit.getthere.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class GeoPlace {

    public String asciiName;
    public LatLng latLng;
    public String countryName;

    public  GeoPlace(){

    }

    public static GeoPlace fromJSON(JSONObject jsonObject){
        GeoPlace geoPlace = new GeoPlace();

        try {
            geoPlace.asciiName = jsonObject.getString("asciiName");
            geoPlace.latLng = new LatLng(
                    Double.parseDouble(jsonObject.getString("lat")),
                    Double.parseDouble(jsonObject.getString("lng")));
            if(jsonObject.has("countryName")){
                geoPlace.countryName = jsonObject.getString("countryName");
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return geoPlace;
    }

    public static List<GeoPlace> fromJSONArray(JSONArray jsonArray){
        List<GeoPlace> geoPlaces = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++){
            try {
                GeoPlace geoPlace = GeoPlace.fromJSON(jsonArray.getJSONObject(i));
                geoPlaces.add(geoPlace);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

        return geoPlaces;
    }
}
