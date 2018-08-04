package com.apps.codeit.getthere.models;

import org.parceler.Parcel;

@Parcel
public class Chauffeur {

    public int ID;
    public String name;
    public String user_name;
    public Position chauffeur_position;

    public Chauffeur(){
        // requires public empty constructor for the parcelable
    }
}
