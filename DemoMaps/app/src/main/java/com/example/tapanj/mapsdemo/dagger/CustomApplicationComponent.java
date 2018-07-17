package com.example.tapanj.mapsdemo.dagger;

import com.example.tapanj.mapsdemo.activities.group.GroupActivity;
import com.example.tapanj.mapsdemo.intentservice.FetchAddressIntentService;
import com.example.tapanj.mapsdemo.intentservice.IntentServiceBase;
import com.example.tapanj.mapsdemo.managers.GroupManager;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Component(modules = {AndroidInjectionModule.class, CustomApplicationModule.class, LoggerModule.class})
public interface CustomApplicationComponent {
    // In the component annotation the modules are specified which are used to create the implementation of components.
    // We also reference the android injection module to ensure the binding of Android base types (Activities, fragmemts etc)
    // Inside the component we should define only the top level dependencies and keep the other dependencies under the hood.
    // Modules provide under the hood dependencies to the outermost dependencies.
    // When you are injecting dependencies into clients who have different lifecycle from where the dependencies are coming, its
    // better to create separate module and component for them.

    // ILogger getLogger();

    void inject(GroupActivity activity);

    // The inject method below can be replaced if we extend the component class with AndroidInjector<CustomApplication>
    void inject (CustomApplication application);

    void inject(IntentServiceBase intentServiceBase);

    void inject(GroupManager groupManager);
}
