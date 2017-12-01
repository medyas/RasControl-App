package com.sabri.com.mini_project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mohamed Yassine on 9/23/2017.
 */



public class SettingsActivity extends AppCompatActivity {

    private EditText ipaddress;
    private TextView port;

    private SharedPreferences prefs;

    String tab = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ipaddress = (EditText) findViewById(R.id.ipaddress);
        port = (TextView) findViewById(R.id.port);

        //getSupportActionBar().show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Back");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // get default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public void redirect(View view) {

        tab = ipaddress.getText().toString()+":"+ port.getText().toString();
        Log.d("tab",tab);

        // redirecting the interface to the main activity
        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("address", tab));
    }

    public void resetFileds(View view) {

        ipaddress.setText("");
        port.setText("");

    }


    @Override
    public void onPause() {
        // save the instance variables
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("ip", ipaddress.getText().toString());
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get preferences
        ipaddress.setText(prefs.getString("ip", "192.168.1."));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
