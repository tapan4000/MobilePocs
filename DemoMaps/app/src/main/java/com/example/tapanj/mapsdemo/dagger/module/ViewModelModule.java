package com.example.tapanj.mapsdemo.dagger.module;

import android.arch.lifecycle.ViewModel;
import com.example.tapanj.mapsdemo.repository.interfaces.ILocationRepository;
import com.example.tapanj.mapsdemo.repository.interfaces.IUserRepository;
import com.example.tapanj.mapsdemo.viewmodel.GroupMemberViewModel;
import com.example.tapanj.mapsdemo.viewmodel.LocationViewModel;
import com.example.tapanj.mapsdemo.viewmodel.UserViewModel;
import com.example.tapanj.mapsdemo.viewmodel.ViewModelFactory;
import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

import javax.inject.Provider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Module
public class ViewModelModule {
    // The implementation of the view model module can be referred from below link
    // https://www.techyourchance.com/dependency-injection-viewmodel-with-dagger-2/
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey{
        Class<? extends ViewModel> value();
    }

    @Provides
    ViewModelFactory viewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModelProviderMap){
        return new ViewModelFactory(viewModelProviderMap);
    }

    @Provides
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    ViewModel userViewModel(IUserRepository userRepository){
        return new UserViewModel(userRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(LocationViewModel.class)
    ViewModel locationViewModel(ILocationRepository locationRepository){
        return new LocationViewModel(locationRepository);
    }

    @Provides
    @IntoMap
    @ViewModelKey(GroupMemberViewModel.class)
    ViewModel groupMemberViewModel(){
        return new GroupMemberViewModel();
    }
}
