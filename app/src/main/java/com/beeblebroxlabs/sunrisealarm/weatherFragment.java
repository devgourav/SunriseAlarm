package com.beeblebroxlabs.sunrisealarm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link weatherFragment.OnFragmentInteractionListener} interface to handle interaction events. Use
 * the {@link weatherFragment#newInstance} factory method to create an instance of this fragment.
 */
public class weatherFragment extends Fragment {
  public static final String WEATHER_API_STATIC_URL_1 = "http://api.openweathermap.org/data/2.5/weather?q=";
  public static final String WEATHER_API_KEY = "cf9e3211132508b56a16c068278590f0";
  public static final String WEATHER_API_STATIC_URL_2 = "&appid=";
  TextView weatherTextView;
  String weatherText;
  private OnFragmentInteractionListener mListener;

  public weatherFragment() {
    // Required empty public constructor
  }

  public static weatherFragment newInstance() {
    weatherFragment fragment = new weatherFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FetchWeatherDataTask fetchWeatherDataTask = new FetchWeatherDataTask();

    String cityName = "Sambalpur";
    String weatherUrl = WEATHER_API_STATIC_URL_1+cityName+WEATHER_API_STATIC_URL_2+WEATHER_API_KEY;
    try {
      weatherText = fetchWeatherDataTask.execute(weatherUrl).get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View weatherView = inflater.inflate(R.layout.fragment_weather, container, false);
    weatherTextView = (TextView)weatherView.findViewById(R.id.weatherText);
    weatherTextView.setText(weatherText);
    return weatherView;
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
