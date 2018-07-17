package com.example.tapanj.mapsdemo.models;

import android.os.Parcel;
import android.os.Parcelable;

public final class Hangout extends Entity implements Parcelable {
    //region All private variables
    private long latitude;
    private long longitude;
    //endregion

    //region Constructors
    public Hangout(String hangoutName){
        this.name = hangoutName;
    }

    private Hangout(Parcel source){
        // Sequence of elements in the parcelable packet: 1) Hangout name
        this.name = source.readString();
    }
    //endregion

    //region Static variables
    public static final Creator<Hangout> CREATOR = new Creator<Hangout>() {
        @Override
        public Hangout createFromParcel(Parcel source) {
            return new Hangout(source);
        }

        @Override
        public Hangout[] newArray(int size) {
            return new Hangout[size];
        }
    };
    //endregion

    //region Public methods
    public String getName(){
        return this.name;
    }
    //endregion

    //region Object overridden methods
    @Override
    public String toString() {
        return this.getName();
    }
    //endregion

    //region Parcelable overridden methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }
    //endregion
}
