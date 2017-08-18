package com.beeblebroxlabs.sunrisealarm;

import android.os.AsyncTask;
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

  public static final int KELVIN_CONST=-273;
  public static final String DEGREE  = "\u00b0";

  public static final String REQUEST_METHOD = "GET";
  public static final int READ_TIMEOUT = 15000;
  public static final int CONNECTION_TIMEOUT = 15000;

  URL url;
  HttpURLConnection httpURLConnection;
  InputStream inputStream;
  InputStreamReader inputStreamReader;
  String weatherJsonResponse = "";

  String weatherDetails = "";
  String cityName = "";

  @Override
  protected String doInBackground(String... urls) {
    try {
      url = new URL(urls[0]);
      httpURLConnection = (HttpURLConnection)url.openConnection();

      //Set methods and timeouts
      httpURLConnection.setRequestMethod(REQUEST_METHOD);
      httpURLConnection.setReadTimeout(READ_TIMEOUT);
      httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);

      httpURLConnection.connect();
      System.out.println("Weather API Response:" + httpURLConnection.getResponseCode() + httpURLConnection.getResponseMessage());


      inputStream = httpURLConnection.getInputStream();
      inputStreamReader = new InputStreamReader(inputStream);

      int data = inputStreamReader.read();
      while(data!=-1){
        char currentChar = (char)data;
        weatherJsonResponse += currentChar;
        data = inputStreamReader.read();
      }
      inputStream.close();
      inputStreamReader.close();
      try {
        JSONObject weatherJsonObject = new JSONObject(weatherJsonResponse);


        JSONObject weatherDescriptionObject = new JSONArray(
            weatherJsonObject.getString("weather")).getJSONObject(0);
        String weatherDescription = weatherDescriptionObject.getString("main");


        JSONObject weatherTemperatureObject = weatherJsonObject.getJSONObject("main");
        int weatherMainTemp = weatherTemperatureObject.getInt("temp");
        weatherMainTemp = weatherMainTemp + KELVIN_CONST;

        cityName = weatherJsonObject.getString("name");


        weatherDetails = cityName + " " + weatherDescription + Integer.toString(weatherMainTemp) +DEGREE+"c";
        System.out.println("weatherDetails:" + weatherDetails);

      } catch (JSONException e) {
        e.printStackTrace();
      }
      return weatherDetails;
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
}
