package com.autstudent.autschedular;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.autstudent.autschedular.Helper.DatabaseTitle;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PaperDescription extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private List<ParseObject> papers;
    private List<ParseObject> streams = new ArrayList<>();
    private int currentSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        papers = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("paper");

        try {
            papers = query.find();
            ArrayList<String> ar = new ArrayList();
            for (ParseObject o : papers) {
                ar.add(o.get("paper_title").toString());
            }
            Spinner spinner = (Spinner) findViewById(R.id.paper_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ar);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.currentSelection = position;
        DisplayPaperList displayPaperList = new DisplayPaperList();
        displayPaperList.execute(position);
    }

    private class DisplayPaperList extends AsyncTask<Integer,Void,Void>{
        private View loadingView;
        private View listView;


        @Override
        protected void onPreExecute() {
            loadingView = findViewById(R.id.loading_class);
            loadingView.setVisibility(View.VISIBLE);
            listView = findViewById(R.id.paper_stream_selector);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Integer... positions) {
            int position = positions[0];
            try {
                ParseObject itemSelected = papers.get(position);
                ParseRelation<ParseObject> relation = itemSelected.getRelation("class");
                ParseQuery<ParseObject> query = relation.getQuery();
                query.orderByAscending("stream_no");
                streams = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final ListView ls = (ListView) findViewById(R.id.paper_stream_selector);
            StreamArrayAdapter streamArrayAdapter = new StreamArrayAdapter(PaperDescription.this, R.layout.custom_paper_row_layout, streams);
            ls.setAdapter(streamArrayAdapter);
            ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("item ps", position + "");
                    StreamArrayAdapter streamArrayAdapter = (StreamArrayAdapter) ls.getAdapter();
                    ParseObject ob = (ParseObject) streamArrayAdapter.getItem(position);
                    if (ob != null) {
                        try {
                            SavingClass savingClass = new SavingClass();
                            savingClass.execute(ob);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            loadingView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        private class SavingClass extends AsyncTask<ParseObject,Void,Void>{

            private ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(PaperDescription.this);
                pd.setMessage("Saving Class");
                pd.show();
            }

            @Override
            protected Void doInBackground(ParseObject... params) {
                try {
                    ParseObject ob = params[0];
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseObject> relation = user.getRelation("Class");
                    relation.add(ob);
                    user.save();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                pd.dismiss();
            }
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nothing
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
