package com.sabri.com.mini_project;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.view.View;
import android.webkit.URLUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.media.MediaPlayer;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.DecimalFormat;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MainActivity extends  AppCompatActivity {

    JoystickView js;

    // set up preferences
    private SharedPreferences prefs;

    TextView textangle, textspeed;
    Switch start, manual;

    // format double
    private static DecimalFormat df2 = new DecimalFormat(".##");

    Boolean startControl = false, gui = false;
    String ip="";
    int port;

    private Intent service;

    Client  connection = new Client();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        service = new Intent(this,  Notification.class);
        startService(service);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        textangle = (TextView)findViewById(R.id.textViewAngle);
        textspeed = (TextView)findViewById(R.id.speed);

        start = (Switch) findViewById(R.id.startstop);
        manual = (Switch) findViewById(R.id.automan);

        js = (JoystickView) findViewById(R.id.joystickView);
        joystick(false); // disabled the joystick


        try {
            String tab = getIntent().getStringExtra("address");

            if(!tab.equals(":")) {
                String t[] = tab.split(":");
                ip = t[0];
                port = Integer.parseInt(t[1]);

                // making a connection to the server to send data
                connection= new Client(ip, port); //New instance of NetworkTask
                connection.editui(this, getApplicationContext());

            }
            else {
                showToast("No server to connect to!");
            }
        }
        catch(Exception e) {
        }


        //getSupportActionBar().show();

        manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    showToast("Using GUI");
                    manual.setText("GUI");
                    joystick(false);
                    gui = true;
                    connection.SendDataToNetwork("gui");
                    connection.SendDataToNetwork("gui");
                }

                else {
                    manual.setText("Man");
                    showToast("Manual Mode");
                    joystick(true);
                    gui = false;
                }
            }
        });

        start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    if(!ip.equals("")) {
                        try {
                            connection.execute();
                            start.setText("Stop");
                            startControl = true;
                            joystick(true); // enable the joystick
                        }
                        catch(Exception e) {
                            Log.d("Restart", "Restarting");
                        }
                    }
                    else {
                        start.setChecked(false);
                        showToast("Please Connect To a Server!");
                    }
                }
                else {
                    connection.SendDataToNetwork("disconnect");
                    connection.cancel();
                    showToast("Stoped!");

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("address", ip+":"+Integer.toString(port));
                    startActivity(i);

                    start.setText("Start");
                    joystick(false); // disable the joystick
                    startControl = false;
                    textangle.setText("Angle: 0°");
                }


            }
        });

        if(!gui) {
            js.setOnMoveListener(new JoystickView.OnMoveListener() {
                @Override
                public void onMove(int angle, int strength, int posx, int posy) {
                    // do whatever you want
                    if(startControl) {
                        textangle.setText("Angle : "+angle+"°");

                        double s = (((int) (js.getW()/2.0) - posy))/(js.getB()/100.0);
                        textspeed.setText(df2.format(s)+" ");

                        connection.SendDataToNetwork(Integer.toString(angle)+":"+Integer.toString(strength)+":"+Double.toString(s)+"\n");
                    }
                    else {
                        Log.d("stop mode", "Stop mode is on!");
                    }

                }
            }, 100);
        }
        else {
            joystick(false); // disabled the joystick
        }

    }


    public void joystick(Boolean b){
        js.setEnabled(b); // disabled the joystick

    }


    // Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }


    // Selecting the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showToast(String msg) {
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context, text, duration).show();
    }

    @Override
    public void onPause() {
        // save the instance variables
        SharedPreferences.Editor editor = prefs.edit();

        //editor.putBoolean("startControl", startControl);
        editor.putString("angle", textangle.getText().toString());
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get preferences
        startControl = prefs.getBoolean("startControl", false);

        textangle.setText(prefs.getString("angle", "Angle: 0°"));
        textspeed.setText(prefs.getString("speed", "Speed "));
        start.setChecked(prefs.getBoolean("startControl", false));


    }

    @Override
    public void onStop(){
        super.onStop();



    }




}