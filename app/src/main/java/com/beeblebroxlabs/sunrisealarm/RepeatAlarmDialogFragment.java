package com.beeblebroxlabs.sunrisealarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class RepeatAlarmDialogFragment extends DialogFragment {
  public RepeatAlarmDialogFragment() {
    // Required empty public constructor
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage("Repeat Alarm")
        .setPositiveButton("Repeat", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            Intent intent = new Intent(getContext(),MainActivity.class);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
           // Toast.makeText(getActivity().getApplicationContext(), "Alarm has been repeated", Toast.LENGTH_SHORT).show();
          }
        })
        .setNegativeButton("Only Once", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            Intent intent = new Intent(getContext(),MainActivity.class);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, intent);
           // Toast.makeText(getActivity().getApplicationContext(), "Alarm single instance", Toast.LENGTH_SHORT).show();
          }
        });
    return builder.create();
  }
}
