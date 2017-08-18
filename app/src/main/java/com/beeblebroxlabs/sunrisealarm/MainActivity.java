package com.beeblebroxlabs.sunrisealarm;


import static android.widget.TextClock.DEFAULT_FORMAT_24_HOUR;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.SyncStateContract.Constants;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.Toast;
import com.beeblebroxlabs.sunrisealarm.WeatherFragment.OnFragmentInteractionListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
    WeatherFragment.OnFragmentInteractionListener, QuoteFragment.OnFragmentInteractionListener,GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

  static final int ADD_RECORD = 1;
  public static final int REQUEST_LOCATION = 100;
  TextClock clockText;
  Boolean is12hourClock = TRUE;

  Location currentLocation;

  private OnFragmentInteractionListener mListener;
  private GoogleApiClient googleApiClient;
  private LocationRequest locationRequest;
  private FusedLocationProviderApi fusedLocationProvider = LocationServices.FusedLocationApi;
  WeatherFragment weatherFragment;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    clockText = (TextClock) findViewById(R.id.clockText);
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivityForResult(new Intent(MainActivity.this, SetAlarmActivity.class), ADD_RECORD);
      }
    });



    weatherFragment = new WeatherFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.weatherFragment_container,weatherFragment,"WEATHER_FRAG");
    fragmentTransaction.add(R.id.quoteFragment_container, new QuoteFragment());
    fragmentTransaction.add(R.id.displayAlarmFragment_container, new AlarmDisplayFragment());
    fragmentTransaction.commit();

    buildGoogleAPIClient();
    buildLocationRequest();


  }



  private synchronized void buildGoogleAPIClient() {

    //Configuring the Google API client before connect
    this.googleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

  }

  private synchronized void buildLocationRequest() {

    this.locationRequest = LocationRequest.create()
        .setInterval(60 * 1000)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setFastestInterval(15 * 1000);

  }

  public void refreshWeatherFragment(Location location) {
    System.out.println("Inside refreshWeatherFragment.");
    Bundle locationBundle = new Bundle();

    weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag("WEATHER_FRAG");

    WeatherFragment newWeatherFragment = new WeatherFragment();

    if(location!=null){
      locationBundle.putDouble("longitude",location.getLongitude());
      locationBundle.putDouble("latitude",location.getLatitude());

      FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      fragmentTransaction.remove(weatherFragment);

      newWeatherFragment.setArguments(locationBundle);

      fragmentTransaction.add(R.id.weatherFragment_container,newWeatherFragment,"WEATHER_FRAG");
      fragmentTransaction.commit();
    }else{
      Log.e("Location null.",location.toString());
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    requestLocationUpdates();
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onLocationChanged(Location location) {
    currentLocation = location;
    System.out.println("onLocationChanged:" + currentLocation);
    refreshWeatherFragment(currentLocation);
  }

  private void requestLocationUpdates() {
    System.out.println("Inside requestLocationUpdates.");
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[]{
              android.Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_LOCATION);
    }else{
      System.out.println("Inside requestLocationUpdates else");
      LocationServices.FusedLocationApi
          .requestLocationUpdates(googleApiClient, locationRequest, this);
    }

  }
  @Override
  public void onStart() {
    super.onStart();
    googleApiClient.connect();
  }

  @Override
  protected void onPause() {
    super.onPause();
    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(googleApiClient.isConnected()){
      requestLocationUpdates();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    googleApiClient.disconnect();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch(requestCode){
      case REQUEST_LOCATION:{
        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
          System.out.println("inside onRequestPermissionsResult granted");
          requestLocationUpdates();
        }else{
          Toast.makeText(this, "Some functionality may not work properly.Enable Data", Toast.LENGTH_SHORT).show();
        }
      }
    }
  }


}
