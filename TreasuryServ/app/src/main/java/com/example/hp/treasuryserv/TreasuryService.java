package com.example.hp.treasuryserv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import com.example.hp.common.IMyAidlInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TreasuryService extends Service {
    private static final String TAG = "TreasuryService";
    private static String STATUS = "";

    public void snooze(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        STATUS = "Destroyed/ Yet to Bind again";
        sendStatus();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        STATUS = "Destroyed / Yet to Bound/ Created";
        sendStatus();

    }

    //function to send status
    public void sendStatus(){

        LocalBroadcastManager localBroadcastManager  = LocalBroadcastManager.getInstance(this);
        Intent i = new Intent("SERVICE_STATUS");
        i.putExtra("STATUS", STATUS);
        localBroadcastManager.sendBroadcast(i);
    }


    @Override
    public IBinder onBind(final Intent intent) {
        STATUS = "Bound";
        sendStatus();

        return new IMyAidlInterface.Stub(){

            //A test function implemented to check connection

            @Override
            public int[] monthlyCash(int year) throws RemoteException{
                TreasuryService.STATUS = "RECEIVING DATA";
                sendStatus();
                snooze(6000);
                int[] result = new int[12];
                String query_1 = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q="+"SELECT \"open_mo\" FROM t1 WHERE \"year\"="+year+" AND \"account\" = \"Total Operating Balance\" Group By \"month\"";
                StringBuilder s = new StringBuilder();
                URL url = null;
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    url = new URL(query_1);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    String line = "";
                    while ((line = reader.readLine()) != null){
                        s.append(line);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println(s.toString());
                for (int i=0; i<12; i++){
                    JSONArray parent = null;
                    try {
                        parent = new JSONArray(s.toString());
                        JSONObject obj = parent.getJSONObject(i);
                        result[i] = obj.getInt("open_mo");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                STATUS = "Bound";
                sendStatus();
                return result;
            }

            @Override
            public int[] dailyCash(int year, int month, int day, int no_of_workingday) throws RemoteException {
                TreasuryService.STATUS = "Getting Data for dailyCash";
                sendStatus();
                snooze(6000);
                String query_2 = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q="+
                        "SELECT \"open_today\" FROM t1 WHERE \"year\"="+year+" AND " +
                        "((\"month\"="+month+" AND \"day\" BETWEEN "+day+" AND 31) OR \"month\"="+(month+1)+" OR \"month\"="+(month+2)+") " +
                        "AND \"account\"=\"Total Operating Balance\" Group by \"year\", \"month\", \"day\"";
                StringBuilder s = new StringBuilder();
                URL url = null;
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    url = new URL(query_2);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    String line = "";
                    while ((line = reader.readLine()) != null){
                        s.append(line);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println(s.toString());
                int[] result = new int[no_of_workingday+1];
                for (int i=0; i<no_of_workingday+1; i++){
                    JSONArray parent = null;
                    try {
                        parent = new JSONArray(s.toString());
                        JSONObject obj = parent.getJSONObject(i);
                        result[i] = obj.getInt("open_today");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                STATUS = "Bound";
                sendStatus();
                return result;
            }

            @Override
            public int yearlyAvg(int year) throws RemoteException {
                TreasuryService.STATUS = "Getting data for yearly average";
                sendStatus();
                snooze(6000);
                String query_3 = "http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=SELECT avg(\"open_today\") FROM t1 WHERE \"year\"="+year;
                StringBuilder s = new StringBuilder();
                URL url = null;
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    url = new URL(query_3);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    String line = "";
                    while ((line = reader.readLine()) != null){
                        s.append(line);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println(s.toString());
                JSONObject first = null;
                float result = 0;
                try {
                    JSONArray parentArray = new JSONArray(s.toString());
                    first = (JSONObject) parentArray.get(0);
                    result = (int) first.getInt("avg(\"open_today\")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                STATUS = "Bound";
                sendStatus();
                return (int) result;
            }
        };
    }
}
