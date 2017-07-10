package com.beeblebroxlabs.sunrisealarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
  public static final int REQUEST_CODE = 100;
  FragmentManager manager;
  View displayAlarmView;
  ListView alarmListView;
  DeleteAlarmDialogFragment deleteAlarmDialogFragment;
  String alarmPosition;



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
    displayAlarmView = inflater.inflate(R.layout.fragment_alarm_display, container, false);
    manager = getFragmentManager();
    deleteAlarmDialogFragment = new DeleteAlarmDialogFragment();
    deleteAlarmDialogFragment.setTargetFragment(this,REQUEST_CODE);


    if(alarmModels.size() == 0){
      Toast.makeText(getContext(), "No alarm set", Toast.LENGTH_SHORT).show();
    }else{
      Toast.makeText(getContext(), "Alarm set", Toast.LENGTH_SHORT).show();
      alarmListView = (ListView)displayAlarmView.findViewById(R.id.alarmListView);
      AdapterInterface deleteSwitchListener = new AdapterInterface()
      {
        @Override
        public void onClick(String value) {
          alarmPosition = value;
          deleteAlarmDialogFragment.show(manager,"DeleteAlarm");
        }
      };
      CustomAlarmListAdapter customListAdapter = new CustomAlarmListAdapter(getActivity(),alarmModels,deleteSwitchListener);
      alarmListView.setAdapter(customListAdapter);
      System.out.println("Tag:" + alarmListView.getTag());
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch(requestCode){
      case REQUEST_CODE:
        if(resultCode == Activity.RESULT_OK){
            sqLiteHelper.deleteRecord(alarmPosition);
          Intent intent = new Intent(getContext(),MainActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);
        }break;
    }
  }
}
