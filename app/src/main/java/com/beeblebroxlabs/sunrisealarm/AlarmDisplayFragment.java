
package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManagerNonConfig;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.beeblebroxlabs.sunrisealarm.CustomAlarmListAdapter.CustomSwitchListenerInterface;
import java.util.ArrayList;


public class AlarmDisplayFragment extends Fragment {

  public static final int REQUEST_CODE = 100;

  SQLiteHelper sqLiteHelper;
  ArrayList<AlarmModel> alarmModels = new ArrayList<AlarmModel>();
  FragmentManager manager;
  View displayAlarmView;
  ListView alarmListView;
  DeleteAlarmDialogFragment deleteAlarmDialogFragment;
  String alarmPosition;
  AlarmManager alarmManager;
  Intent alarmIntent;
  CustomAlarmListAdapter customListAdapter;


  private OnFragmentInteractionListener mListener;

  public AlarmDisplayFragment() {
  }

  public static AlarmDisplayFragment newInstance() {
    AlarmDisplayFragment fragment = new AlarmDisplayFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sqLiteHelper = new SQLiteHelper(getActivity());
    alarmModels = sqLiteHelper.getAllAlarmRecords();
    alarmIntent = new Intent(getContext(),AlarmReceiver.class);
    alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    displayAlarmView = inflater.inflate(R.layout.fragment_alarm_display, container, false);

    manager = getFragmentManager();
    deleteAlarmDialogFragment = new DeleteAlarmDialogFragment();
    deleteAlarmDialogFragment.setTargetFragment(this, REQUEST_CODE);

    String noOfAlarmSet = Integer.toString(alarmModels.size());
    Log.e("NoOfAlarmSet:", noOfAlarmSet);
    if (!noOfAlarmSet.equals(0)) {
      alarmListView = (ListView) displayAlarmView.findViewById(R.id.alarmListView);
      CustomSwitchListenerInterface deleteSwitchListener = new CustomSwitchListenerInterface() {
        @Override
        public void onClick(String value) {
          alarmPosition = value;
          deleteAlarmDialogFragment.show(manager, "DeleteAlarm");
        }
      };

      CustomSwitchListenerInterface disableSwitchListener = new CustomSwitchListenerInterface() {
        @Override
        public void onClick(String value) {
          AlarmModel alarmModel;
          alarmModel = sqLiteHelper.getAlarmRecord(value);
          alarmIntent.putExtra("alarmHour",alarmModel.getAlarmHour());
          alarmIntent.putExtra("alarmMinute",alarmModel.getAlarmMinute());
          alarmIntent.putExtra("alarmLabel",alarmModel.getAlarmLabel());
          alarmIntent.putExtra("isAlarmSet", alarmModel.getAlarmEnabled());
          alarmIntent.putExtra("alarmId",alarmModel.getAlarmId());

          System.out.println("alarmModel.getAlarmId():"+alarmModel.getAlarmId());

          int alarmId = Integer.valueOf(alarmModel.getAlarmId());

          PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),alarmId,
              alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);

          alarmManager.cancel(alarmPendingIntent);

        }
      };
      customListAdapter = new CustomAlarmListAdapter(getActivity(),
          alarmModels, deleteSwitchListener,disableSwitchListener);
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
    switch (requestCode) {
      case REQUEST_CODE:
        if (resultCode == Activity.RESULT_OK) {

//          Intent intent = new Intent(getContext(), MainActivity.class);
//          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//          startActivity(intent);
          AlarmModel alarmModel = sqLiteHelper.getAlarmRecord(alarmPosition);
          customListAdapter.remove(alarmModel);
          sqLiteHelper.deleteRecord(alarmPosition);
        }
        break;
    }
  }


  @Override
  public void onResume() {
    super.onResume();
    customListAdapter.notifyDataSetChanged();
  }
}
