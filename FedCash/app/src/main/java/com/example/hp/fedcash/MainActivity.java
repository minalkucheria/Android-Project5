package com.example.hp.fedcash;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hp.common.IMyAidlInterface;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MainActivity extends AppCompatActivity {
    public RadioGroup radioGroup;
    private boolean first=true,bind=false;
    CyclicBarrier barrier = new CyclicBarrier(2);
    private WorkerThread t1;
    TextView resultview;
    public EditText edittext1,edittext2,edittext3,edittext4;
    private Handler UIHandler = new Handler(Looper.getMainLooper());
    IMyAidlInterface mIMyAidlInterface;
    int year,month,day,no_of_working_days;
    public Handler wHandler;
    private final Object lock = new Object();
    int selected_option=0;
    String TAG="1",q;
    IMyAidlInterface treasuryservinterface;

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            treasuryservinterface = IMyAidlInterface.Stub.asInterface(service);
            bind=true;
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Service has unexpectedly disconnected");
            treasuryservinterface = null;
            bind=false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext1 = (EditText) findViewById(R.id.edit_year);
        edittext2 = (EditText) findViewById(R.id.edit_day);
        edittext3 = (EditText) findViewById(R.id.edit_month);
        edittext4 = (EditText) findViewById(R.id.edit_workingdays);
        edittext1.setEnabled(false);
        edittext2.setEnabled(false);
        edittext3.setEnabled(false);
        edittext4.setEnabled(false);
        resultview=(TextView)findViewById(R.id.textview_result);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        Button getResults=(Button)findViewById(R.id.getResults);
        Button b=(Button)findViewById(R.id.button);
        getResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bind){
                    Bind();
                }
                synchronized (lock){
                    String y;
                switch (selected_option){
                    case 1:
                        y=edittext1.getText().toString();
                        year = Integer.parseInt(y);
                        Log.i("Year",String.valueOf(year));
                        wHandler.sendEmptyMessage(selected_option);
                        break;
                    case 2:
                         y=edittext1.getText().toString();
                        year = Integer.parseInt(y);
                        String m=edittext2.getText().toString();
                        month = Integer.parseInt(m);
                        String d=edittext3.getText().toString();
                        day = Integer.parseInt(d);
                        String n=edittext4.getText().toString();
                        no_of_working_days = Integer.parseInt(n);
                        wHandler.sendEmptyMessage(selected_option);
                        break;
                    case 3:
                        y=edittext1.getText().toString();
                        year = Integer.parseInt(y);
                        wHandler.sendEmptyMessage(selected_option);
                         break;
                }

            }}
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Activity 2
                Intent i = new Intent(MainActivity.this, PastQR.class);
                startActivity(i);
            }
        });
        t1 = new WorkerThread(barrier);
        t1.start();
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.monthly_cash:
                if (checked)
                    edittext1.setEnabled(true);
                    edittext2.setEnabled(false);
                    edittext3.setEnabled(false);
                    edittext4.setEnabled(false);
                    selected_option=1;
                    break;
            case R.id.daily_cash:
                if (checked){
                    edittext1.setEnabled(true);
                    edittext2.setEnabled(true);
                    edittext3.setEnabled(true);
                    edittext4.setEnabled(true);
                    selected_option=2;
                    break;
                }
            case R.id.yearly_avg:
                if (checked)
                    edittext1.setEnabled(true);
                    edittext2.setEnabled(false);
                    edittext3.setEnabled(false);
                    edittext4.setEnabled(false);
                    selected_option=3;
                    break;
        }
    }
    public void Bind(){
        //Bind to service
        synchronized (lock){
            Intent intent = new Intent(IMyAidlInterface.class.getName());
            ResolveInfo info = getPackageManager().resolveService(intent,Context.BIND_AUTO_CREATE);
            intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
            intent.putExtra("Fedcash", true);
            first = false;
            boolean b = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }
    }

    public class WorkerThread extends Thread {
        StringBuilder s;
        String q;
        private CyclicBarrier mBarrier;

        public WorkerThread(CyclicBarrier barrier) {
            mBarrier = barrier;
        }
        public WorkerThread(String name) {
            super(name);
        }
        public void run(){
            Looper.prepare();

            wHandler=new Handler(){

                public void handleMessage(Message msg){
                    s = new StringBuilder();
                    while (treasuryservinterface == null){
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    switch(msg.what){
                        case 1: q="Get cash details for all months "+ year +".";
                            int[] result=new int[12];
                            try {
                                result = treasuryservinterface.monthlyCash(year);
                            }catch (RemoteException e)
                            {
                                e.printStackTrace();
                            }
                            s.append("Month wise data for the year given above is ");
                            for (int i=0; i<result.length; i++){
                                int cash = result[i];
                                s.append(""+ cash);
                            }
                            s.append("million USD");
                            break;
                        case 2:
                            q = "Get cash details for "+ no_of_working_days+" workingdays after date " + month + "/" + day +"/"+ year +".";
                            int[] result2=new int[no_of_working_days+1];
                            try {
                                result2 = treasuryservinterface.dailyCash(year,month,day,no_of_working_days);
                            }catch (RemoteException e)
                            {
                                e.printStackTrace();
                            }
                            s.append("Data for specified working days starting from specified date is ");
                            for (int i=0; i<result2.length; i++){
                                int cash = result2[i];
                                System.out.println(cash);
                                s.append(""+ cash);
                            }
                            s.append("million USD");
                            break;
                        case 3:
                            q = "Get the average of cash on all days of the year "+ year +".";
                            int result3=0;
                            try {
                                result3 = treasuryservinterface.yearlyAvg(year);
                            }catch (RemoteException e)
                            {
                                e.printStackTrace();
                            }
                            s.append("Average of all working days cash for a year is ");
                            s.append(""+result3);
                            s.append(" million USD");
                            break;
                         default:
                             break;
                    }
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                          resultview.setText(s.toString());
                        }
                    });
                    addIntoSharedPreferences(q,s.toString());
                }
              //  addIntoSharedPreferences(q,s.toString());

            };
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            Looper.loop();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    //This will make sure that there is no leaked service connection.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    public void addIntoSharedPreferences(String query, String result){
        SharedPreferences pastq = this.getSharedPreferences("com.example.fedcash.PAST_QUERIES", Context.MODE_PRIVATE);
        Set<String> allQueriesOldCopy = pastq.getStringSet("pastQuerySet", new HashSet<String>());
        Set<String> oldset = new HashSet<String>(allQueriesOldCopy);
        oldset.add(query);
        SharedPreferences.Editor editor = pastq.edit();
        editor.putString(query,result);
        editor.putStringSet("pastQuerySet",oldset);
        editor.commit();
    }


}
