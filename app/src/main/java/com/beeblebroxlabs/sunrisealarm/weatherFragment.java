package com.beeblebroxlabs.sunrisealarm;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface to handle interaction events. Use
 * the {@link WeatherFragment#newInstance} factory method to create an instance of this fragment.
 */
public class WeatherFragment extends Fragment{

  public static final String WEATHER_API_STATIC_URL_1 = "http://api.openweathermap.org/data/2.5/weather?";
  public static final String WEATHER_API_KEY = "cf9e3211132508b56a16c068278590f0";
  public static final String WEATHER_API_STATIC_URL_2 = "&appid=";
  public static final int KELVIN_CONST=-273;
  public static final String DEGREE  = "\u00b0";
  public static final int REQUEST_LOCATION = 100;


  TextView weatherTextView;
  String weatherText,weatherJsonResponse;
  Location currentLocation;
  Double latitude, longitude;
  SharedPreferences sharedPreferences;
  Context context;

  private OnFragmentInteractionListener mListener;



  public WeatherFragment() {
    // Required empty public constructor
  }

  public static WeatherFragment newInstance() {
    WeatherFragment fragment = new WeatherFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle arguments = getArguments();
    currentLocation = new Location("serviceprovider");


    if(arguments!=null && arguments.containsKey("longitude")){
      longitude = arguments.getDouble("longitude");
      latitude = arguments.getDouble("latitude");
      currentLocation.setLongitude(longitude);
      currentLocation.setLatitude(latitude);
      getWeatherInfo(currentLocation);
    }else{
      longitude = 0.0;
      latitude = 0.0;
      weatherText = " ";
    }



  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);
    weatherTextView = (TextView) weatherView.findViewById(R.id.weatherText);
    weatherTextView.setText(weatherText);

    return weatherView;
  }

  public String getWeatherInfo(Location location) {
    FetchWeatherDataTask fetchWeatherDataTask = new FetchWeatherDataTask();
    context = getActivity().getApplicationContext();
    latitude = location.getLatitude();
    longitude = location.getLongitude();


    sharedPreferences = context.getSharedPreferences("myPref",MODE_PRIVATE);
    Double oldLatitude = Double.valueOf( sharedPreferences.getString("latitude","0") );
    Double oldLongitude = Double.valueOf( sharedPreferences.getString("longitude","0" ));

    Double longitudeDelta = Math.abs(oldLongitude-longitude);
    Double latitudeDelta = Math.abs(oldLatitude-latitude);

    if(longitudeDelta>0.2 || latitudeDelta>0.2) {
      String weatherUrl =
          WEATHER_API_STATIC_URL_1 + "lat=" + latitude + "&lon=" + longitude
              + WEATHER_API_STATIC_URL_2 + WEATHER_API_KEY;
      try {
        weatherJsonResponse = fetchWeatherDataTask.execute(weatherUrl).get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
      System.out.println("weatherJsonResponse if:"+weatherJsonResponse);
    }else{
      weatherJsonResponse = sharedPreferences.getString("weatherJsonResponse",weatherJsonResponse);
      System.out.println("weatherJsonResponse else:"+weatherJsonResponse);
    }



    try {
      JSONObject weatherJsonObject = new JSONObject(weatherJsonResponse);
      JSONObject weatherDescriptionObject = new JSONArray(
          weatherJsonObject.getString("weather")).getJSONObject(0);
      String weatherDescription = weatherDescriptionObject.getString("main");
      JSONObject weatherTemperatureObject = weatherJsonObject.getJSONObject("main");
      int weatherMainTemp = weatherTemperatureObject.getInt("temp");
      weatherMainTemp = weatherMainTemp + KELVIN_CONST;

      String cityName = weatherJsonObject.getString("name");
      JSONObject weatherSysObject = weatherJsonObject.getJSONObject("sys");
      String sunriseTime = weatherSysObject.getString("sunrise");


      String st = sharedPreferences.getString("sunriseTime","default");
      System.out.println("Sunrise time set:"+st);


      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      Boolean isFahrenheit = sharedPreferences.getBoolean("temperatureUnit",FALSE);

      Double temperature;
      String temperatureDetails;
      if(isFahrenheit==TRUE){
        temperature = (weatherMainTemp*1.8)+32;
        temperatureDetails = Double.toString(temperature)+DEGREE+"F";

      }else{
        temperatureDetails = Integer.toString(weatherMainTemp)+DEGREE+"C";
      }


      sharedPreferences = context.getSharedPreferences("myPref",MODE_PRIVATE);
      Editor editor = sharedPreferences.edit();
      editor.putString("weatherJsonResponse",weatherJsonResponse);
      editor.putString("cityName",cityName);
      editor.putString("sunriseTime",sunriseTime);
      editor.putString("latitude", Double.toString(latitude));
      editor.putString("longitude",Double.toString(longitude));
      editor.commit();


      weatherText = cityName + " " + weatherDescription + " "+temperatureDetails;
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return weatherText;
  }


  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnFragmentInteractionListener {

    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }


}
