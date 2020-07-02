package com.kl.cameraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kl.cameraapp.view.FragmentCamera;
import com.kl.cameraapp.view.MapsFragment;

public class MainActivity extends AppCompatActivity {
    final FragmentCamera fragCamera;
    final MapsFragment fragMap;
    BottomNavigationView botNav;
    Fragment active;
    final FragmentManager fm;
    public MainActivity(){
        fragCamera = new FragmentCamera();
        fragMap = new MapsFragment();
        fm = getSupportFragmentManager();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botNav = findViewById(R.id.bot_nav);

        fm.beginTransaction().add(R.id.frame_container, fragMap).hide(fragMap).commit();
        fm.beginTransaction().add(R.id.frame_container, fragCamera).commit();
        active = fragCamera;

        botNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottomNavCam:
                        fm.beginTransaction().hide(active).show(fragCamera).commit();
                        active = fragCamera;
                        return true;

                    case R.id.bottomNavMap:
                        fm.beginTransaction().hide(active).show(fragMap).commit();
                        active = fragMap;
                        return true;
                }
                return false;
            }

        });

    }


}
