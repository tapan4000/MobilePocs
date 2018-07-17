package com.example.tapanj.mapsdemo.managers;

import com.example.tapanj.mapsdemo.interfaces.integration.IGroupAdapter;
import com.example.tapanj.mapsdemo.models.Group;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

public class GroupManager {
    @Inject
    IGroupAdapter groupAdapter;

    public void getAllGroups(Consumer<List<Group>> onSuccessCallbackHandler, Consumer<String> onFailureCallbackHandler){
        this.groupAdapter.getGroups(onSuccessCallbackHandler, onFailureCallbackHandler);
    }
}
