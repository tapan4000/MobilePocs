package com.example.tapanj.mapsdemo.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.arch.persistence.room.Entity;

@Entity(tableName = "GroupMember")
public final class GroupMember extends EntityBase implements Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "GroupMemberId")
    private int groupMemberId;

    @ColumnInfo(name = "GroupId")
    private int groupId;

    @ColumnInfo(name = "UserId")
    private int userId;

    public GroupMember(int groupMemberId){
        this.groupMemberId = groupMemberId;
    }

    @Ignore
    //region Constructors
    public GroupMember(String memberName) {
        this.name = memberName;
    }

    @Ignore
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

    public int getGroupMemberId(){
        return this.groupMemberId;
    }

    public int getGroupId(){
        return this.groupId;
    }

    public int getUserId(){
        return this.userId;
    }

    public void setGroupId(int groupId){
        this.groupId = groupId;
    }

    public void setUserId(int userId){
        this.userId = userId;
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
