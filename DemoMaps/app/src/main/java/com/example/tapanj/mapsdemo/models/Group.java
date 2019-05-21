package com.example.tapanj.mapsdemo.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.tapanj.mapsdemo.models.dao.EntityBase;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;

import java.util.ArrayList;
import java.util.List;

public class Group extends EntityBase implements Parcelable {
    //region All private variables
    private List<Hangout> locations;
    private List<GroupMember> members;
    //endregion

    //region Constructors
    public Group(String name){
        this.name = name;
    }

    public Group(String name, List<GroupMember> members, List<Hangout> hangouts){
        this.name = name;
        this.members = members;
        this.locations = hangouts;
    }

    private Group(Parcel source) {
        // Sequence of elements in the Parcelable packet: 1) Member list 2) Group Name
        this.members = new ArrayList<GroupMember>();
        source.readTypedList(this.members, GroupMember.CREATOR);

        this.locations = new ArrayList<>();
        source.readTypedList(this.locations, Hangout.CREATOR);

        this.name = source.readString();
    }
    //endregion

    //region Static members
    public final static Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>(){
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
    //endregion

    //region Public methods
    public String getName(){
        return this.name;
    }

    public List<GroupMember> getGroupMembers(){
        return this.members;
    }

    public List<Hangout> getGroupHangouts(){
        return this.locations;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean isMemberAlreadyAdded(GroupMember member){
        if(null == this.members){
            return false;
        }

        // Validate if the member has already been added based on the member identifier.
        for (GroupMember objMember: this.members) {
            if(objMember.name == member.name){
                return true;
            }
        }

        return false;
    }

    public boolean addMember(GroupMember member){
        if(null == this.members){
            this.members = new ArrayList<>();
        }

        if(this.isMemberAlreadyAdded(member)){
            return false;
        }

        this.members.add(member);
        return true;
    }

    public boolean isHangoutAlreadyAdded(Hangout hangout){
        if(null == this.locations){
            return false;
        }

        // Validate if the member has already been added based on the member identifier.
        for (Hangout objHangout: this.locations) {
            if(objHangout.name == hangout.name){
                return true;
            }
        }

        return false;
    }

    public boolean addHangout(Hangout hangout){
        if(null == this.locations){
            this.locations = new ArrayList<>();
        }

        if(this.isHangoutAlreadyAdded(hangout)){
            return false;
        }

        this.locations.add(hangout);
        return true;
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
        dest.writeTypedList(this.members);
        dest.writeTypedList(this.locations);
        dest.writeString(this.name);
    }
    //endregion
}
