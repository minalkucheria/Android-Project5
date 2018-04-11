package com.example.hp.fedcash;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Created by HP on 12/6/2017.
 */

public class QFragment extends ListFragment implements AdapterView.OnItemClickListener {

        ListSelectionInterface mCallback;
        PastQR pastqueries;
        String[] pq;


@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        }

@Override
public void onAttach(Context context) {
        super.onAttach(context);
        try {
        mCallback = (ListSelectionInterface) context;

        } catch (ClassCastException e) {
        throw new ClassCastException(context.toString()
        + " must implement OnArticleSelectedListener");
        }
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.listview_fragment, container, false);
        pastqueries = (PastQR) getActivity();
        pq = pastqueries.pastqStringArray;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, pq);
        setListAdapter(adapter);
        return v;
        }

@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(this);
        }

public interface ListSelectionInterface{
    public void displayResult(int position);
}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCallback.displayResult(position);
    }
}

