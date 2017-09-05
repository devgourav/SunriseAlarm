package com.beeblebroxlabs.sunrisealarm;


import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.Manifest.permission;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextClock;
import android.widget.Toast;
import com.beeblebroxlabs.sunrisealarm.WeatherFragment.OnFragmentInteractionListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
    WeatherFragment.OnFragmentInteractionListener, QuoteFragment.OnFragmentInteractionListener,GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private static final int ADD_RECORD = 1;
  private static final int REQUEST_LOCATION = 100;
  private static final int LOCATION_NORMAL_INTERVAL = 600 * 1000;
  private static final int LOCATION_FASTEST_INTERVAL = 150 * 1000;
  private static final String LOG_ERROR = "LOG_ERROR";
  private static final String LOG_INFO = "LOG_INFO";

  private Location currentLocation;
  private OnFragmentInteractionListener mListener;
  private GoogleApiClient googleApiClient;
  private LocationRequest locationRequest;
  Boolean isMilitaryTimeFormat = FALSE;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);


    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivityForResult(new Intent(MainActivity.this, SetAlarmActivity.class), ADD_RECORD);
      }
    });



    refreshclockFormat();


    //Build the initial main display before any permissions.
    WeatherFragment weatherFragment = new WeatherFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.weatherFragment_container,weatherFragment,"WEATHER_FRAGMENT");
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
        .setInterval(LOCATION_NORMAL_INTERVAL)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setFastestInterval(LOCATION_FASTEST_INTERVAL);
  }

  private void refreshclockFormat() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    isMilitaryTimeFormat = sharedPreferences.getBoolean("24hourClock",FALSE);
    TextClock clockText = (TextClock) findViewById(R.id.clockText);
    if(isMilitaryTimeFormat){
      clockText.setFormat12Hour("HH:mm");
    }else{
      clockText.setFormat24Hour("hh:mm");
    }
  }

  public void refreshWeatherFragment(Location location) {
    Bundle locationBundle = new Bundle();
    WeatherFragment weatherFragment = (WeatherFragment) getSupportFragmentManager()
        .findFragmentByTag("WEATHER_FRAGMENT");
    WeatherFragment refreshedWeatherFragment = new WeatherFragment();

    if(location == null) {
      location = requestLastLocation();
    }else{
      Log.e("Location Data Fetched","");
    }

    locationBundle.putDouble("longitude",location.getLongitude());
    locationBundle.putDouble("latitude",location.getLatitude());

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.remove(weatherFragment);

    refreshedWeatherFragment.setArguments(locationBundle);

    fragmentTransaction.add(R.id.weatherFragment_container,refreshedWeatherFragment,"WEATHER_FRAGMENT");
    fragmentTransaction.commit();

  }



  @Override
  public void onConnected(@Nullable Bundle bundle) {
    requestLocationUpdates();
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.e(LOG_ERROR,"Connection Suspended");
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.e(LOG_ERROR,"Connection Failed");

  }

  @Override
  public void onLocationChanged(Location location) {
    Log.i(LOG_INFO,"Location Changed:"+location);
    currentLocation = location;
    refreshWeatherFragment(currentLocation);
  }

  public void requestLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_LOCATION);
    }else{
      LocationServices.FusedLocationApi
          .requestLocationUpdates(googleApiClient, locationRequest, this);
    }
  }

  public Location requestLastLocation() {
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          REQUEST_LOCATION);
      return null;
    }else{
      return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }
  }
  @Override
  public void onStart() {
    super.onStart();
    this.googleApiClient.connect();
  }

  @Override
  protected void onPause() {
    super.onPause();
    //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
  }

  @Override
  protected void onResume() {
    super.onResume();
//    if(this.googleApiClient.isConnected()){
//      requestLocationUpdates();
//    }
    refreshclockFormat();
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
          requestLocationUpdates();
        }else{
          Toast.makeText(this, "Location not Enabled.Weather data will not be fetched.", Toast.LENGTH_SHORT).show();
        }
      }
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }
}
