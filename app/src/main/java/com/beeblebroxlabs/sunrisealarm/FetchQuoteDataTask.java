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

public class FetchQuoteDataTask extends AsyncTask<String,Void,String>{


  public static final String REQUEST_METHOD = "GET";
  public static final int READ_TIMEOUT = 15000;
  public static final int CONNECTION_TIMEOUT = 15000;

  String quoteJsonResponse = "",quote = "";;


  @Override
  protected String doInBackground(String... urls) {
//    try {
//      URL url = new URL(urls[0]);
//      HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
//
//      //Set methods and timeouts
//      httpURLConnection.setRequestMethod(REQUEST_METHOD);
//      httpURLConnection.setReadTimeout(READ_TIMEOUT);
//      httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
//
//      InputStream inputStream = httpURLConnection.getInputStream();
//      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//
//      int data = inputStreamReader.read();
//      while(data!=-1){
//        char currentChar = (char)data;
//        quoteJsonResponse += currentChar;
//        data = inputStreamReader.read();
//      }
//      inputStream.close();
//      inputStreamReader.close();
//      try {
//        JSONObject quoteJsonObject = new JSONObject(quoteJsonResponse);
//        String quoteContents = quoteJsonObject.getString("contents");
//        JSONObject quoteContentJsonObject = new JSONObject(quoteContents);
//        JSONObject quoteObject = new JSONArray(
//            quoteContentJsonObject.getString("quotes")).getJSONObject(0);
//
//        quote = quoteObject.getString("quote");
//
//
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
//      return quote;
//    } catch (MalformedURLException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return null;

    return "\"Do not be afraid to give up the good for the great.\"";
  }

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);
  }
}
