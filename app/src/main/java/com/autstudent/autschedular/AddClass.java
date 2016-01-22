package com.autstudent.autschedular;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by wilzo on 11/01/2016.
 */
public class AddClass extends AddActivity.PlaceholderFragment{

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_class_add,container,false);
        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        RefreshClassList refreshClassList = new RefreshClassList();
        refreshClassList.execute();
    }

    private class RefreshClassList extends AsyncTask<Void,Void,Void>{

        private View progress;
        private View list;
        private List<ParseObject> objectLists;

        @Override
        protected void onPreExecute() {
            progress = rootView.findViewById(R.id.progress_layout);
            progress.setVisibility(View.VISIBLE);
            list = rootView.findViewById(R.id.add_class_list);
            list.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation <ParseObject> relation = user.getRelation("Class");
            ParseQuery<ParseObject>query = relation.getQuery();
            query.include("paper");
            try {
                objectLists = query.find();
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ListView ls = (ListView) getActivity().findViewById(R.id.add_class_list);
            StreamArrayAdapter streamArrayAdapter = new StreamArrayAdapter(getActivity(), R.layout.custom_paper_row_layout, objectLists);
            ls.setAdapter(streamArrayAdapter);
            progress.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }
}
