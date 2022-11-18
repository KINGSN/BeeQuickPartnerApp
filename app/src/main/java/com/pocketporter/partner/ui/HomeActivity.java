package com.pocketporter.partner.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pocketporter.partner.R;
import com.pocketporter.partner.frgment.AccountFragment;
import com.pocketporter.partner.frgment.HomeFragment;
import com.pocketporter.partner.frgment.OtherFragment;
import com.pocketporter.partner.utility.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bottomNavigation.setSelectedItemId(R.id.navigation_pendding);

    }

    public boolean openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_pendding:
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && Utility.hasGPSDevice(this)) {
                    Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
                    Utility.enableLoc(this);
                    return false;
                } else {
                    fragment = new HomeFragment();
                }

                break;
            case R.id.navigation_notifications:
                fragment = new OtherFragment();
                break;
            case R.id.navigation_profile:
                fragment = new AccountFragment();
                break;
            default:
                break;
        }
        return openFragment(fragment);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}