package com.example.tapanj.mapsdemo.models.user;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import com.example.tapanj.mapsdemo.models.dao.EntityBase;

@Entity(tableName = "UserTable")
public final class UserTable extends EntityBase implements Parcelable {
    // TODO: Check if the UserTable entity needs to be Parcelable in the scenario where the UserTable object needs to be passed between activities/fragments.
    // Limited fields will be stored on the mobile client based on the usage. The auth token and refresh token would be saved
    // separately using secure method.
    // UserTable data would be stored in SQLite database as there are multiple fields to be stored. If we use SharedPreferences we need to fetch
    // each field separately vs the SQLite where we can fetch the entire row.
    // UserTable Id can be used to fetch the user record based on the logged in user.
    @PrimaryKey
    @ColumnInfo(name = "UserId")
    private int userId;

    // The isd code will be used during user settings display.
    @ColumnInfo(name = "IsdCode")
    private String isdCode;

    // The mobile number will be used during user settings display.
    @ColumnInfo(name = "MobileNumber")
    private String mobileNumber;

    // The email will be used during user settings display.
    @ColumnInfo(name = "Email")
    private String email;

    // The first name will be used during logged in user information display.
    @ColumnInfo(name = "FirstName")
    private String firstName;

    // The last name will be used during logged in user information display.
    @ColumnInfo(name = "LastName")
    private String lastName;

    // The user state can be used to display specific UI.
    @ColumnInfo(name = "UserStateId")
    private int userStateId;

    // The membership tier can be used to display specific UI.
    @ColumnInfo(name = "MembershipTierId")
    private int membershipTierId;

    @Ignore
    private UserTable(Parcel source) {
        this.userId = source.readInt();
        this.isdCode = source.readString();
        this.mobileNumber = source.readString();
        this.email = source.readString();
        this.firstName = source.readString();
        this.lastName = source.readString();
        this.userStateId = source.readInt();
        this.membershipTierId = source.readInt();
    }

    @Ignore
    public UserTable(int userId, String isdCode, String mobileNumber, String email, String firstName, String lastName, int userStateId, int membershipTierId){
        this.userId = userId;
        this.isdCode = isdCode;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userStateId = userStateId;
        this.membershipTierId = membershipTierId;
    }

    public UserTable(){
    }

    public static final Creator<UserTable> CREATOR = new Creator<UserTable>() {
        @Override
        public UserTable createFromParcel(Parcel source) {
            return new UserTable(source);
        }

        @Override
        public UserTable[] newArray(int size) {
            return new UserTable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeString(this.isdCode);
        dest.writeString(this.mobileNumber);
        dest.writeString(this.email);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeInt(this.userStateId);
        dest.writeInt(this.membershipTierId);
    }

    // Getter methods
    public int getUserId(){
        return this.userId;
    }

    public String getIsdCode(){
        return this.isdCode;
    }

    public String getMobileNumber(){
        return this.mobileNumber;
    }

    public String getEmail(){
        return this.email;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public int getUserStateId(){
        return this.userStateId;
    }

    public int getMembershipTierId(){
        return this.membershipTierId;
    }

    // Setter methods
    public void setUserId(int userId){
        this.userId = userId;
    }

    public void setIsdCode(String isdCode){
        this.isdCode = isdCode;
    }

    public void setMobileNumber(String mobileNumber){
        this.mobileNumber = mobileNumber;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setUserStateId(int userStateId){
        this.userStateId = userStateId;
    }

    public void setMembershipTierId(int membershipTierId){
        this.membershipTierId = membershipTierId;
    }
}
