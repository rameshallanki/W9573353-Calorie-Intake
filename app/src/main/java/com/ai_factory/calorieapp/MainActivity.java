package com.ai_factory.calorieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navViewHome;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_home);
        navViewHome = findViewById(R.id.nav_view_home);

        fstore=FirebaseFirestore.getInstance();

        loadFragment(new Home());

        navViewHome.setNavigationItemSelectedListener(this);
        //for drawer Button
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        //showing drawer
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        Fragment fragment = null;
        drawerLayout.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(this, breakfast_page.class);
        switch (item.getItemId()) {
            case R.id.bottom_home:
                fragment = new Home();
                break;
            case R.id.bottom_profile:
                fragment = new Profile();
                break;
            case R.id.breakfastBtn:
                intent.putExtra("type", "BreakFast");
                startActivity(intent);
                break;
            case R.id.lunchBtn:
                intent.putExtra("type", "Lunch");
                startActivity(intent);
                break;
            case R.id.DinnerBtn:
                intent.putExtra("type", "Dinner");
                startActivity(intent);
                break;
            case R.id.location:
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    buildAlertMessageNoGps();
                break;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1004);
                    break;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1005);
                    break;
                }
                else {
                    intent = new Intent(this, MapsActivity.class);
                    startActivity(intent);
                }break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,Splash.class));
                finish();
                break;
        }
        if (fragment != null) {
            loadFragment(fragment);
        }
        return true;
    }
    void loadFragment(Fragment fragment) {
        //to attach fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.home_relative_layout, fragment).commit();
    }



    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}

//API
//https://www.programmableweb.com/category/nutrition/api
//https://calorieninjas.com/api
//https://fdc.nal.usda.gov/api-guide.html

// Design
//https://github.com/rahulmaddineni/Stayfit
//https://nevonprojects.com/calorie-calculator-suggester-android-app/