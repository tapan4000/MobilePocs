package com.example.tapanj.mapsdemo.models;

import android.os.Parcel;
import android.os.Parcelable;

public final class GroupMember extends Entity implements Parcelable {
    //region Constructors
    public GroupMember(String memberName) {
        this.name = memberName;
    }

    private GroupMember(Parcel source) {
        // Sequence of elements in the parcelable packet: 1) Group member name
        this.name = source.readString();
    }
    //endregion

    //region Static variables
    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel source) {
            return new GroupMember(source);
        }

        @Override
        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };
    // endregion

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

    //region Overridden Parcelable methods
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
