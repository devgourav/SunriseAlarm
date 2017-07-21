package com.beeblebroxlabs.sunrisealarm;


import static android.widget.TextClock.DEFAULT_FORMAT_24_HOUR;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.net.Uri;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements weatherFragment.OnFragmentInteractionListener,QuoteFragment.OnFragmentInteractionListener{
  static final int ADD_RECORD = 1;
  TextClock clockText;
  Boolean is12hourClock = TRUE;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    clockText = (TextClock)findViewById(R.id.clockText);

    if(is12hourClock){
      clockText.setFormat12Hour("h:mm aa");
    }else{
      clockText.setFormat24Hour("k:mm");
    }



    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
        startActivityForResult(new Intent(MainActivity.this,SetAlarmActivity.class),ADD_RECORD);
      }
    });





    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.weatherFragment_container,new weatherFragment());
    fragmentTransaction.add(R.id.quoteFragment_container,new QuoteFragment());
    fragmentTransaction.add(R.id.displayAlarmFragment_container,new AlarmDisplayFragment());
    fragmentTransaction.commit();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }
}
