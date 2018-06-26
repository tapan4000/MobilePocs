package com.example.tapanj.mapsdemo;

import java.util.List;

public class Group extends Entity {
    private List<Hangout> mHangouts;
    private List<GroupMember> mMembers;

    public Group(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
