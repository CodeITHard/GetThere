package com.apps.codeit.getthere.models;

import org.parceler.Parcel;

@Parcel
public class User {

    public int ID;
    public String name;
    public String user_name;
    public Position user_position;

    public User(){
        // requires public empty constructor for the parcelable
    }
}
