package com.apps.codeit.getthere.models;

import org.parceler.Parcel;

@Parcel
public class Position {

    public float lng;
    public float lat;

    public Position(){
        // requires public empty constructor for the parcelable
    }
}
