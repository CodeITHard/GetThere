package com.apps.codeit.getthere.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Place {
    /*
    {

        "address_components": [
        {
            "long_name": "Bombay",
                "short_name": "Bombay",
                "types": [
            "locality",
                    "political"
            ]
        },
        {
            "long_name": "District de Mumbai-ville",
                "short_name": "District de Mumbai-ville",
                "types": [
            "administrative_area_level_2",
                    "political"
            ]
        },
        {
            "long_name": "Maharashtra",
                "short_name": "MH",
                "types": [
            "administrative_area_level_1",
                    "political"
            ]
        },
        {
            "long_name": "Inde",
                "short_name": "IN",
                "types": [
            "country",
                    "political"
            ]
        }
    ],
        "formatted_address": "Bombay, Maharashtra, Inde",
            "geometry": {
        "bounds": {
            "northeast": {
                "lat": 19.2716339,
                        "lng": 72.9864994
            },
            "southwest": {
                "lat": 18.8928676,
                        "lng": 72.7758729
            }
        },
        "location": {
            "lat": 19.0759837,
                    "lng": 72.87765589999999
        },
        "location_type": "APPROXIMATE",
                "viewport": {
            "northeast": {
                "lat": 19.2716339,
                        "lng": 72.9864994
            },
            "southwest": {
                "lat": 18.8928676,
                        "lng": 72.7758729
            }
        }
    },
        "place_id": "ChIJwe1EZjDG5zsRaYxkjY_tpF0",
            "types": [
        "locality",
                "political"
    ]

    }
    */

    public String long_name[];
    public String formatted_address;
    public double lat;
    public double lng;

    public Place(){
    }

    public static Place fromJSON(JSONObject jsonObject){
        Place place = new Place();
        JSONArray jsonArray;

        try {
            jsonArray = jsonObject.getJSONArray("address_components");
            //place.long_name = jsonObject.getJSONArray("address_components").getJSONObject(0).getString("long_name");
            place.long_name = new String[jsonArray.length()];
            for (int i=0;i<jsonArray.length();i++){
                place.long_name[i] = jsonArray.getJSONObject(i).getString("long_name");
            }
            place.formatted_address = jsonObject.getString("formatted_address");
            place.lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            place.lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return place;
    }

    public static ArrayList<Place> fromJSONArray(JSONArray jsonArray){
        ArrayList<Place> places = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++){
            try {
                places.add(Place.fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return places;
    }
}
