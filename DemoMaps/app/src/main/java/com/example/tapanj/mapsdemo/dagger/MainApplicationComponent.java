package com.example.tapanj.mapsdemo.dagger;

import android.app.Application;
import androidx.work.Worker;
import com.example.tapanj.mapsdemo.activities.ActivityBase;
import com.example.tapanj.mapsdemo.activities.group.GroupActivity;
import com.example.tapanj.mapsdemo.activities.group.GroupListActivity;
import com.example.tapanj.mapsdemo.activities.group.GroupMemberActivity;
import com.example.tapanj.mapsdemo.activities.map.MapsActivity;
import com.example.tapanj.mapsdemo.dagger.module.*;
import com.example.tapanj.mapsdemo.dagger.module.activity.ActivityModule;
import com.example.tapanj.mapsdemo.dagger.module.activity.GroupActivityModule;
import com.example.tapanj.mapsdemo.dagger.module.fragment.FragmentModule;
import com.example.tapanj.mapsdemo.dagger.module.worker.AndroidWorkerInjectionModule;
import com.example.tapanj.mapsdemo.dagger.module.worker.WorkerModule;
import com.example.tapanj.mapsdemo.dagger.subcomponent.GroupActivitySubcomponent;
import com.example.tapanj.mapsdemo.dagger.subcomponent.PeriodicLocationFetchWorkerSubcomponent;
import com.example.tapanj.mapsdemo.intentservice.IntentServiceBase;
import com.example.tapanj.mapsdemo.managers.GroupManager;
import com.example.tapanj.mapsdemo.workmanager.PeriodicLocationFetchWorker;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Component(modules = {AndroidInjectionModule.class, MainApplicationModule.class, ContributeActivityModule.class,
        LoggerModule.class, GroupManagementModule.class, NetModule.class, LocationModule.class, DataStoreModule.class,
        GroupActivityModule.class, UserManagementModule.class, ViewModelModule.class, ActivityModule.class, FragmentModule.class,
        ServiceModule.class, AndroidWorkerInjectionModule.class, WorkerModule.class, BroadcastReceiverModule.class})
public interface MainApplicationComponent extends AndroidInjector<MainApplication> {
    // Refer following link for reference: https://google.github.io/dagger/android.html
    // In the component annotation the modules are specified which are used to create the implementation of components.
    // We also reference the android injection module to ensure the binding of Android base types (Activities, fragmemts etc)
    // Inside the component we should define only the top level dependencies and keep the other dependencies under the hood.
    // Modules provide under the hood dependencies to the outermost dependencies.
    // When you are injecting dependencies into clients who have different lifecycle from where the dependencies are coming, its
    // better to create separate module and component for them.

    // ILogger getLogger();

    //void inject(ActivityBase activity);
    //void inject(GroupActivity activity);

    //void inject(GroupListActivity activity);

    //void inject(GroupMemberActivity activity);

    //void inject(MapsActivity activity);

    // The inject method below can be replaced if we extend the component class with AndroidInjector<CustomApplication>
    //void inject (MainApplication application);

    //void inject(IntentServiceBase intentServiceBase);

    //void inject(GroupManager groupManager);

    //void inject(Worker worker);

    //void inject(GroupMemberRepository groupMemberRepository);

    @Component.Builder
    interface Builder
    {
        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder backendUrl(@Named("backendUrl")String backendUrl);

        MainApplicationComponent build();
    }
}
