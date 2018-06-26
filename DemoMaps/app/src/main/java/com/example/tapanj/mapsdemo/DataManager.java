package com.example.tapanj.mapsdemo;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager dataManagerInstance;
    private List<Group> mGroups = new ArrayList<>();

    public static DataManager getInstance(){
        if(dataManagerInstance == null){
            dataManagerInstance = new DataManager();
            dataManagerInstance.initializeGroups();
        }

        return dataManagerInstance;
    }

    public List<Group> getGroups(){
        return mGroups;
    }

    public boolean addNewGroup(String groupName){
        mGroups.add(new Group(groupName));
        return true;
    }

    private void initializeGroups(){
        mGroups.add(new Group("My home group"));
        mGroups.add(new Group("My work group"));
        mGroups.add(new Group("Farzi group"));
        mGroups.add(new Group("Elegant Group"));
        mGroups.add(new Group("Security Group"));
        mGroups.add(new Group("Friends Group"));
    }
}
