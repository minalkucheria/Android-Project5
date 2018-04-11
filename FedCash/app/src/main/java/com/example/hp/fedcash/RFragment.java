package com.example.hp.fedcash;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by HP on 12/6/2017.
 */

public class RFragment extends Fragment {
    private fragment_interaction interact;
    private TextView res;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {

            // Set the ListSelectionListener for communicating with the QuoteViewerActivity
            interact = (fragment_interaction) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.text_fragment, null, false);
        res = (TextView) v.findViewById(R.id.textview_result);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setResult(String result){
        res.setText(result);
    }

    public interface fragment_interaction{
        public void f();
    }
}
