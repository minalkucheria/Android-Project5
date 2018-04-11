package com.example.hp.fedcash;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HP on 12/6/2017.
 */

public class PastQR extends Activity implements RFragment.fragment_interaction, QFragment.ListSelectionInterface{
    public RFragment rFragment = new RFragment();
    public QFragment qFragment = new QFragment();
    public String[] pastqStringArray;
    public ArrayList<String> pastResults = new ArrayList<String>();
   // public String[] pq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);
        //Getting data from shared preferences and populating data arrays
        SharedPreferences pastq = this.getSharedPreferences("com.example.fedcash.PAST_QUERIES", Context.MODE_PRIVATE);
        Set<String> allQueriesOldCopy = pastq.getStringSet("pastQuerySet", new HashSet<String>());
        Set<String> newSet = new HashSet<String>(allQueriesOldCopy);
        pastqStringArray = newSet.toArray(new String[0]);
        for (int i=0; i<pastqStringArray.length; i++){
            pastResults.add(pastq.getString(pastqStringArray[i],null));
        }

        //ADding fragments
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.result,rFragment);
        transaction.add(R.id.past,qFragment);
        transaction.commit();

    }

    //It does nothing
    @Override
    public void f() {

    }

    //Displaying result on the right side
    @Override
    public void displayResult(int position) {
        Toast.makeText(this,""+position, Toast.LENGTH_SHORT).show();
        rFragment.setResult(pastResults.get(position).toString());
    }
}

