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
 * {@link QuoteFragment.OnFragmentInteractionListener} interface to handle interaction events. Use
 * the {@link QuoteFragment#newInstance} factory method to create an instance of this fragment.
 */
public class QuoteFragment extends Fragment {
  TextView quoteTextView;
  String quoteText;

  public static final String QUOTE_API_URL = "http://quotes.rest/qod.json?category=inspire";


  private OnFragmentInteractionListener mListener;

    public QuoteFragment() {
      // Required empty public constructor
    }

  public static QuoteFragment newInstance() {
    QuoteFragment fragment = new QuoteFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FetchQuoteDataTask fetchQuoteDataTask = new FetchQuoteDataTask();

    try {
      quoteText = fetchQuoteDataTask.execute(QUOTE_API_URL).get();
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
    View quoteView = inflater.inflate(R.layout.fragment_quote, container, false);
    quoteTextView = (TextView)quoteView.findViewById(R.id.quoteText);
    quoteTextView.setText(quoteText);
    return quoteView;
  }

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
    void onFragmentInteraction(Uri uri);
  }
}
