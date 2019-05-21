package com.example.tapanj.mapsdemo.activities.user;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.tapanj.mapsdemo.R;
import com.example.tapanj.mapsdemo.activities.ActivityBase;
import com.example.tapanj.mapsdemo.activities.FragmentActivityBase;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.SharedPreferenceConstants;
import com.example.tapanj.mapsdemo.models.user.User;
import com.example.tapanj.mapsdemo.repository.Resource;
import com.example.tapanj.mapsdemo.viewmodel.UserViewModel;
import dagger.android.AndroidInjection;

public class UserLoginRegisterActivity extends FragmentActivityBase {
    private static final int NUM_LOGIN_REGISTER_PAGES = 2;

    // The pager widget that that handles animation and allows swiping horizontally.
    private ViewPager loginRegisterViewPager;

    // The pager adapter that provides the pages to the view pager widget.
    private PagerAdapter loginRegisterPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);

        // Instantiate the ViewPager and PagerAdapter
        this.loginRegisterViewPager = (ViewPager) findViewById(R.id.viewpager_loginRegister);
        this.loginRegisterPagerAdapter = new LoginRegisterSlidePagerAdapter(getSupportFragmentManager());
        this.loginRegisterViewPager.setAdapter(this.loginRegisterPagerAdapter);




    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void initializeFragmentActivityLifecycleWorkflowContext() {

    }

    @Override
    protected void injectMembers() {
        AndroidInjection.inject(this);
    }

    private class LoginRegisterSlidePagerAdapter extends FragmentStatePagerAdapter
    {

        public LoginRegisterSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return new UserLoginFragment();
            }

            return new UserRegistrationFragment();
        }

        @Override
        public int getCount() {
            return NUM_LOGIN_REGISTER_PAGES;
        }
    }
}
