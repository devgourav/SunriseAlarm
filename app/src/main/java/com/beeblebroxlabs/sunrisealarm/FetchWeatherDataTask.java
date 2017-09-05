package com.beeblebroxlabs.sunrisealarm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaActionSound;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by devgr on 05-Jul-17.
 */

public class FetchWeatherDataTask extends AsyncTask<String,Void,String>{



  public static final String REQUEST_METHOD = "GET";
  public static final int READ_TIMEOUT = 15000;
  public static final int CONNECTION_TIMEOUT = 15000;

  public String sunriseTime;
  SharedPreferences sharedPreferences;
  private Context mContext;
  private Activity mActivity;


  String weatherJsonResponse = " ",cityName = " ";
  String weatherDetails = " ";

  public FetchWeatherDataTask() {
  }


  @Override
  protected String doInBackground(String... urls) {
    try {

      URL url = new URL(urls[0]);
      HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

      //Set methods and timeouts
      httpURLConnection.setRequestMethod(REQUEST_METHOD);
      httpURLConnection.setReadTimeout(READ_TIMEOUT);
      httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
      httpURLConnection.connect();


      InputStream inputStream = httpURLConnection.getInputStream();
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

      int data = inputStreamReader.read();
      while(data!=-1){
        char currentChar = (char)data;
        weatherJsonResponse += currentChar;
        data = inputStreamReader.read();
      }
      inputStream.close();
      inputStreamReader.close();


//      try {
//        JSONObject weatherJsonObject = new JSONObject(weatherJsonResponse);
//        JSONObject weatherDescriptionObject = new JSONArray(
//            weatherJsonObject.getString("weather")).getJSONObject(0);
//        String weatherDescription = weatherDescriptionObject.getString("main");
//        JSONObject weatherTemperatureObject = weatherJsonObject.getJSONObject("main");
//        int weatherMainTemp = weatherTemperatureObject.getInt("temp");
//        weatherMainTemp = weatherMainTemp + KELVIN_CONST;
//
//        cityName = weatherJsonObject.getString("name");
//        JSONObject weatherSysObject = weatherJsonObject.getJSONObject("sys");
//        sunriseTime = weatherSysObject.getString("sunrise");
//
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        Editor editor = sharedPreferences.edit();
//        editor.putString("sunriseTime",sunriseTime);
//        editor.commit();
//
//        String st = sharedPreferences.getString("sunriseTime","Default");
//        System.out.println("Sunrise time set:"+st);
//
//
//        weatherDetails = cityName + " " + weatherDescription +
//            Integer.toString(weatherMainTemp) +DEGREE+"c";
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
      return weatherJsonResponse;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);

  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
  }
}
