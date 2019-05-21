package com.example.tapanj.mapsdemo.managers;

import com.example.tapanj.mapsdemo.models.Group;
import com.example.tapanj.mapsdemo.models.dao.GroupMember;
import com.example.tapanj.mapsdemo.models.Hangout;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    //region Private static variables
    private static DataManager dataManagerInstance;
    //endregion

    //region Private variables
    private List<Group> mGroups = new ArrayList<>();
    //endregion

    //region Public static methods
    public static DataManager getInstance(){
        if(dataManagerInstance == null){
            dataManagerInstance = new DataManager();
            dataManagerInstance.initializeGroups();
        }

        return dataManagerInstance;
    }
    //endregion

    //region Public instance methods
    public List<Group> getGroups(){
        return mGroups;
    }

    public boolean addNewGroup(String groupName){
        mGroups.add(new Group(groupName));
        return true;
    }
    //endregion

    //region Private instance methods
    private void initializeGroups(){
        int counter = 1;
        mGroups.add(getGroupInstance(counter));
        counter++;
        mGroups.add(getGroupInstance(counter));
        counter++;
        mGroups.add(getGroupInstance(counter));
        counter++;
        mGroups.add(getGroupInstance(counter));
        counter++;
        mGroups.add(getGroupInstance(counter));
        counter++;
        mGroups.add(getGroupInstance(counter));
        counter++;
        mGroups.add(getGroupInstance(counter));
        counter++;
        mGroups.add(getGroupInstance(counter));
    }

    private Group getGroupInstance(int groupIndex){
        Group group = new Group("My Sample Group" + groupIndex);

        int childCounter = 1;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));
        childCounter++;
        group.addMember(new GroupMember("My Sample member" + childCounter));

        childCounter = 1;
        group.addHangout(new Hangout("My hangout" + childCounter));
        childCounter++;
        group.addHangout(new Hangout("My hangout" + childCounter));
        childCounter++;
        group.addHangout(new Hangout("My hangout" + childCounter));
        childCounter++;
        group.addHangout(new Hangout("My hangout" + childCounter));
        childCounter++;
        group.addHangout(new Hangout("My hangout" + childCounter));
        childCounter++;
        group.addHangout(new Hangout("My hangout" + childCounter));
        childCounter++;
        group.addHangout(new Hangout("My hangout" + childCounter));

        return group;
    }
    //endregion
}
