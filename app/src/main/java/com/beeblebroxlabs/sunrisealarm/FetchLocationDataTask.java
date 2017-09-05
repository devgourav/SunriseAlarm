package com.beeblebroxlabs.sunrisealarm;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by devgr on 03-Aug-17.
 */

public class FetchLocationDataTask extends AsyncTask<Void,Void,Location> {

  private FusedLocationProviderClient mFusedLocationClient;
  Location currentLocation;
  private Context mContext;
  private Activity mActivity;


  public FetchLocationDataTask(Context context, Activity activity) {
    mContext = context;
    mActivity = activity;
  }

  @Override
  protected Location doInBackground(Void... params) {
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

    if (ActivityCompat.checkSelfPermission(mActivity,
        permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(mActivity,
          new String[]{permission.ACCESS_FINE_LOCATION},1);

    }else{
      mFusedLocationClient.getLastLocation()
          .addOnSuccessListener(new OnSuccessListener<Location>() {
            public void onSuccess(Location location) {
              currentLocation = location;
            }
          });
    }
    return currentLocation;
  }
}
