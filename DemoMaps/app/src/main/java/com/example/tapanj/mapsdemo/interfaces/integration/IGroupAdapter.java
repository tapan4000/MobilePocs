package com.example.tapanj.mapsdemo.interfaces.integration;

import com.example.tapanj.mapsdemo.models.Group;

import java.util.List;
import java.util.function.Consumer;

public interface IGroupAdapter {
    void getGroups(Consumer<List<Group>> onSuccessCallbackHandler, Consumer<String> onFailureCallbackHandler);
}
