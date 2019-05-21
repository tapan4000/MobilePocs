package com.example.tapanj.mapsdemo.models.dao;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import com.example.tapanj.mapsdemo.common.converters.InstantConverter;
import org.threeten.bp.Instant;

import java.util.Date;

@Entity(tableName = "UserLocationSessionDetail")
public class UserLocationSessionDetail extends EntityBase implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UserLocationSessionDetailId")
    private int userLocationSessionDetailId;

    @ColumnInfo(name = "UserLocationSessionLocalId")
    private String userLocationSessionLocalId;

    @ColumnInfo(name = "EncryptedLatitude")
    public String encryptedLatitude;

    @ColumnInfo(name = "EncryptedLongitude")
    public String encryptedLongitude;

    @ColumnInfo(name = "EncryptedSpeed")
    public String encryptedSpeed;

    @ColumnInfo(name = "EncryptedAltitude")
    public String encryptedAltitude;

    @ColumnInfo(name = "Timestamp")
    @TypeConverters({InstantConverter.class})
    public Instant timestamp;

    protected UserLocationSessionDetail(Parcel in) {
        userLocationSessionLocalId = in.readString();
        encryptedLatitude = in.readString();
        encryptedLongitude = in.readString();
        encryptedSpeed = in.readString();
        encryptedAltitude = in.readString();
    }

    public UserLocationSessionDetail(){

    }

    public static final Creator<UserLocationSessionDetail> CREATOR = new Creator<UserLocationSessionDetail>() {
        @Override
        public UserLocationSessionDetail createFromParcel(Parcel in) {
            return new UserLocationSessionDetail(in);
        }

        @Override
        public UserLocationSessionDetail[] newArray(int size) {
            return new UserLocationSessionDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userLocationSessionLocalId);
        parcel.writeString(encryptedLatitude);
        parcel.writeString(encryptedLongitude);
        parcel.writeString(encryptedSpeed);
        parcel.writeString(encryptedAltitude);
    }

    public int getUserLocationSessionDetailId() {
        return userLocationSessionDetailId;
    }

    public String getUserLocationSessionLocalId() {
        return userLocationSessionLocalId;
    }

    public String getEncryptedLatitude() {
        return encryptedLatitude;
    }

    public String getEncryptedLongitude() {
        return encryptedLongitude;
    }

    public String getEncryptedSpeed() {
        return encryptedSpeed;
    }

    public String getEncryptedAltitude() {
        return encryptedAltitude;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setUserLocationSessionDetailId(int userLocationSessionDetailId) {
        this.userLocationSessionDetailId = userLocationSessionDetailId;
    }

    public void setUserLocationSessionLocalId(String userLocationSessionLocalId) {
        this.userLocationSessionLocalId = userLocationSessionLocalId;
    }

    public void setEncryptedLatitude(String encryptedLatitude) {
        this.encryptedLatitude = encryptedLatitude;
    }

    public void setEncryptedLongitude(String encryptedLongitude) {
        this.encryptedLongitude = encryptedLongitude;
    }

    public void setEncryptedSpeed(String encryptedSpeed) {
        this.encryptedSpeed = encryptedSpeed;
    }

    public void setEncryptedAltitude(String encryptedAltitude) {
        this.encryptedAltitude = encryptedAltitude;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
