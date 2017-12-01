package com.sabri.com.mini_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DecimalFormat;

/**
 * Created by Mohamed Yassine on 9/23/2017.
 */

public class Client extends AsyncTask<Void, String, Boolean> {
    Socket nsocket; //Network Socket
    InputStream nis; //Network Input Stream
    OutputStream nos; //Network Output Stream

    // check the oncancelled method
    boolean cancel = false;

    private String ip;
    int port;

    // *****
    private Activity rootAct;
    private Context rootcont;
    Notification notify = new Notification();


    public Client() {
        ip = "";
        port = 0;
    }

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void editui(Activity act, Context context) {

        rootAct = act;
        rootcont = context;
    }


    public void cancel() {
        onCancelled();
    }

    public boolean connected() {
        if(nsocket == null)
            return false;
        else
            return nsocket.isConnected();
    }


    public void SendDataToNetwork(String data) { //You run this from the main thread.
        try {
            if (nsocket.isConnected()) {
                Log.i("AsyncTask", "SendDataToNetwork: Writing received message to socket "+ data);
                nos.write(data.getBytes());
            } else {
                Log.i("AsyncTask", "SendDataToNetwork: Cannot send message. Socket is closed");
            }
        } catch (Exception e) {
            Log.i("AsyncTask", "SendDataToNetwork: Message send failed. Caught an exception");
        }
    }

    public void showToast(String msg) {
        Context context = rootcont;
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context, text, duration).show();
    }


    // ******

    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "onPreExecute");
        notify.setAct(rootAct);
        notify.not("Trying to Connect to: "+ip, 0);
    }

    @Override
    protected Boolean doInBackground(Void... params) { //This runs on a different thread
        boolean result = false;
        try {

            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            nsocket = new Socket();
            nsocket.connect(sockaddr, 5000);

            if (nsocket.isConnected()) {
                // notification
                notify.not("Connected.", 1);
                nis = nsocket.getInputStream();
                nos = nsocket.getOutputStream();
                Log.i("AsyncTask", "doInBackground: Socket created, streams assigned");
                Log.i("AsyncTask", "doInBackground: Waiting for inital data...");
                byte[] buffer = new byte[4096];
                int read = nis.read(buffer, 0, 4096); //This is blocking
                while(read != -1){
                    byte[] tempdata = new byte[read];
                    System.arraycopy(buffer, 0, tempdata, 0, read);
                    String s = new String(tempdata);
                    notify.not("Data Received.", 3);
                    Log.i("AsyncTask", "doInBackground: Got some data");
                    publishProgress(s);
                    try {
                        read = nis.read(buffer, 0, 4096); //This is blocking
                    }
                    catch(Exception e){}
                }
            }
            else {
                publishProgress("nc");
                throw new Exception();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: IOException");
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: Exception");
            result = true;
        } finally {
            try {
                nis.close();
                nos.close();
                nsocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("AsyncTask", "doInBackground: Finished");
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {

        if(values[0].equals("nc")){

            showToast("Couldn't not connect to server. Please check if the server is working.");
            notify.not("Couldn't connect to: "+ip, 0);
        }
        else
            Log.i("received", "Data : "+values[0]);

    }

    @Override
    protected void onCancelled() {
        try {
            cancel = true;
            nis.close();
            nos.close();
            nsocket.close();
            // notification
            notify.not("Disconnected.", 2);
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Log.i("AsyncTask", "Cancelled.");
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.i("AsyncTask", "onPostExecute: Completed with an Error.");

            if(!cancel) {
                notify.not("Couldn't connect to: "+ip, 0);

                Switch start = (Switch) rootAct.findViewById(R.id.startstop);
                start.setChecked(false);
                start.setText("Start");

                Intent i = rootAct.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( rootAct.getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("address", ip+":"+Integer.toString(port));
                rootAct.startActivity(i);
            }

        } else {
            Log.i("AsyncTask", "onPostExecute: Completed.");
        }

    }


}

