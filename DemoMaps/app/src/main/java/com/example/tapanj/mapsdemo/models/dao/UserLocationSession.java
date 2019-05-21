package com.example.tapanj.mapsdemo.models.dao;

import android.arch.persistence.room.*;
import android.os.Parcel;
import android.os.Parcelable;
import com.example.tapanj.mapsdemo.common.converters.InstantConverter;
import com.example.tapanj.mapsdemo.common.converters.LocationCaptureSessionStateEnumConverter;
import com.example.tapanj.mapsdemo.common.converters.LocationCaptureTypeEnumConverter;
import com.example.tapanj.mapsdemo.enums.LocationCaptureSessionStateEnum;
import com.example.tapanj.mapsdemo.enums.LocationCaptureTypeEnum;
import org.jetbrains.annotations.NotNull;

import org.threeten.bp.Instant;

@Entity(tableName = "UserLocationSession")
public  class UserLocationSession extends EntityBase implements Parcelable {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "LocationSessionLocalId")
    private String locationSessionLocalId;

    @ColumnInfo(name = "UserId")
    private int userId;

    @ColumnInfo(name = "LocationRequestId")
    private String locationRequestId;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "ExpiryDateTime")
    @TypeConverters({InstantConverter.class})
    private Instant expiryDateTime;

    @ColumnInfo(name = "LocationRequestorUserId")
    private int locationRequestorUserId;

    @ColumnInfo(name = "LocationCaptureSessionStateId")
    @TypeConverters({LocationCaptureSessionStateEnumConverter.class})
    private LocationCaptureSessionStateEnum locationCaptureSessionStateId;

    @ColumnInfo(name = "LocationCaptureTypeId")
    @TypeConverters({LocationCaptureTypeEnumConverter.class})
    private LocationCaptureTypeEnum locationCaptureTypeId;

    @ColumnInfo(name = "GroupId")
    private int groupId;

    @ColumnInfo(name = "RequestDateTime")
    @TypeConverters({InstantConverter.class})
    private Instant requestDateTime;

    @ColumnInfo(name = "StoppedBy")
    private String stoppedBy;

    @ColumnInfo(name = "StopDateTime")
    @TypeConverters({InstantConverter.class})
    private Instant stopDateTime;

    protected UserLocationSession(Parcel in) {
        locationSessionLocalId = in.readString();
        userId = in.readInt();
        locationRequestId = in.readString();
        title = in.readString();
        locationRequestorUserId = in.readInt();
        stoppedBy = in.readString();
    }

    public UserLocationSession(){}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locationSessionLocalId);
        dest.writeInt(userId);
        dest.writeString(locationRequestId);
        dest.writeString(title);
        dest.writeInt(locationRequestorUserId);
        dest.writeString(stoppedBy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserLocationSession> CREATOR = new Creator<UserLocationSession>() {
        @Override
        public UserLocationSession createFromParcel(Parcel in) {
            return new UserLocationSession(in);
        }

        @Override
        public UserLocationSession[] newArray(int size) {
            return new UserLocationSession[size];
        }
    };

    public String getLocationSessionLocalId(){
        return this.locationSessionLocalId;
    }

    public int getUserId(){
        return this.userId;
    }

    public String getLocationRequestId(){
        return this.locationRequestId;
    }

    public String getTitle(){
        return this.title;
    }

    public Instant getExpiryDateTime(){
        return this.expiryDateTime;
    }

    public int getLocationRequestorUserId(){
        return this.locationRequestorUserId;
    }

    public LocationCaptureSessionStateEnum getLocationCaptureSessionStateId(){ return this.locationCaptureSessionStateId; }

    public LocationCaptureTypeEnum getLocationCaptureTypeId(){
        return this.locationCaptureTypeId;
    }

    public int getGroupId(){ return this.groupId; }

    public Instant getRequestDateTime(){
        return this.requestDateTime;
    }

    public String getStoppedBy(){
        return this.stoppedBy;
    }

    public Instant getStopDateTime(){
        return this.stopDateTime;
    }

    public void setLocationSessionLocalId(String locationSessionLocalId) {
        this.locationSessionLocalId = locationSessionLocalId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLocationRequestId(String locationRequestId) {
        this.locationRequestId = locationRequestId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setExpiryDateTime(Instant expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    public void setLocationRequestorUserId(int locationRequestorUserId) {
        this.locationRequestorUserId = locationRequestorUserId;
    }

    public void setLocationCaptureSessionStateId(LocationCaptureSessionStateEnum locationCaptureSessionStateId) {
        this.locationCaptureSessionStateId = locationCaptureSessionStateId;
    }

    public void setLocationCaptureTypeId(LocationCaptureTypeEnum locationCaptureTypeId) {
        this.locationCaptureTypeId = locationCaptureTypeId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setRequestDateTime(Instant requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public void setStoppedBy(String stoppedBy) {
        this.stoppedBy = stoppedBy;
    }

    public void setStopDateTime(Instant stopDateTime) {
        this.stopDateTime = stopDateTime;
    }
}
