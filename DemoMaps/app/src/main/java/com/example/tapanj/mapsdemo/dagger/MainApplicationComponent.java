package com.example.tapanj.mapsdemo.dagger;

import android.app.Application;
import androidx.work.Worker;
import com.example.tapanj.mapsdemo.activities.ActivityBase;
import com.example.tapanj.mapsdemo.activities.group.GroupActivity;
import com.example.tapanj.mapsdemo.activities.group.GroupListActivity;
import com.example.tapanj.mapsdemo.activities.group.GroupMemberActivity;
import com.example.tapanj.mapsdemo.activities.map.MapsActivity;
import com.example.tapanj.mapsdemo.dagger.module.*;
import com.example.tapanj.mapsdemo.intentservice.IntentServiceBase;
import com.example.tapanj.mapsdemo.managers.GroupManager;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

import javax.inject.Singleton;

@Singleton
@Component(dependencies = {}, modules = {AndroidInjectionModule.class, LoggerModule.class,
        RoomModule.class, GroupManagementModule.class, NetModule.class, LocationModule.class, DataStoreModule.class})
public interface MainApplicationComponent {
    // In the component annotation the modules are specified which are used to create the implementation of components.
    // We also reference the android injection module to ensure the binding of Android base types (Activities, fragmemts etc)
    // Inside the component we should define only the top level dependencies and keep the other dependencies under the hood.
    // Modules provide under the hood dependencies to the outermost dependencies.
    // When you are injecting dependencies into clients who have different lifecycle from where the dependencies are coming, its
    // better to create separate module and component for them.

    // ILogger getLogger();

    //void inject(ActivityBase activity);
    void inject(GroupActivity activity);

    void inject(GroupListActivity activity);

    void inject(GroupMemberActivity activity);

    void inject(MapsActivity activity);

    // The inject method below can be replaced if we extend the component class with AndroidInjector<CustomApplication>
    void inject (MainApplication application);

    void inject(IntentServiceBase intentServiceBase);

    void inject(GroupManager groupManager);

    void inject(Worker worker);

    //void inject(GroupMemberRepository groupMemberRepository);

    @Component.Builder
    interface Builder{
        MainApplicationComponent build();

        @BindsInstance
        Builder application(Application application);

        Builder netModule(NetModule netModule);
    }
}
