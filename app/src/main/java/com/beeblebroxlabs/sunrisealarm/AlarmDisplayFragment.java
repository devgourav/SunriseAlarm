package com.beeblebroxlabs.sunrisealarm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.beeblebroxlabs.sunrisealarm.CustomAlarmListAdapter.AdapterInterface;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AlarmDisplayFragment extends Fragment {

  SQLiteHelper sqLiteHelper;
  ArrayList<AlarmModel> alarmModels = new ArrayList<AlarmModel>();
  FragmentManager fragmentManager = getFragmentManager();


  private OnFragmentInteractionListener mListener;

  public AlarmDisplayFragment() {}

  public static AlarmDisplayFragment newInstance() {
    AlarmDisplayFragment fragment = new AlarmDisplayFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sqLiteHelper = new SQLiteHelper(getActivity());
    alarmModels = sqLiteHelper.getAllAlarmRecords();


  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View displayAlarmView = inflater.inflate(R.layout.fragment_alarm_display, container, false);
    final FragmentManager manager = getFragmentManager();


    if(alarmModels.size() == 0){
      Toast.makeText(getContext(), "No alarm set", Toast.LENGTH_SHORT).show();
    }else{
      Toast.makeText(getContext(), "Alarm set", Toast.LENGTH_SHORT).show();
      ListView alarmListView = (ListView)displayAlarmView.findViewById(R.id.alarmListView);
      AdapterInterface deleteSwitchListener = new AdapterInterface()
      {
        @Override
        public void onClick(String value)
        {
          DeleteAlarmDialogFragment deleteAlarmDialogFragment = new DeleteAlarmDialogFragment();
          deleteAlarmDialogFragment.show(manager,"DeleteAlarm");
        }
      };
      CustomAlarmListAdapter customListAdapter = new CustomAlarmListAdapter(getActivity(),alarmModels,deleteSwitchListener);
      alarmListView.setAdapter(customListAdapter);
      return displayAlarmView;
    }
    return null;
  }

  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

//  @Override
//  public void onAttach(Context context) {
//    super.onAttach(context);
//    if (context instanceof OnFragmentInteractionListener) {
//      mListener = (OnFragmentInteractionListener) context;
//    } else {
//      throw new RuntimeException(context.toString()
//          + " must implement OnFragmentInteractionListener");
//    }
//  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}
