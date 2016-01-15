package com.autstudent.autschedular;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.autstudent.autschedular.Helper.DatabaseTitle;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private ParseObject ob;
    private ParseObject ob2;

    //Strings for Details Activity
    private String Title = "";
    private String StartTime = "";
    private String EndTime = "";
    private String Note = "";
    private String Date = "";

    private String paperCodeString = "";



    final static String REMINDER_MESSAGE = "MINUTES BEFORE EVENT";
    String a = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //white fix to back arrow
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int idRef = getIntent().getIntExtra("id_ref", -1);
        try {
            if (idRef >= 10000) {
                //checking for if is schedule event
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseTitle.TABLESCHEDULENAME);
                query.whereEqualTo("idRef", idRef);
                ob = query.find().get(0);

                //Change Prim Val For Text Views
                Note = ob.get("Note").toString();
                EndTime = ob.get("EndTime").toString();
                StartTime = ob.get("StartTime").toString();
                Title = ob.get("Title").toString();
                Date = ob.get("Date").toString();
            } else {
                //checking for if is class event
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseTitle.TABLECLASSNAME);
                query.whereEqualTo("idRef", idRef);
                ob = query.find().get(0);

                ParseQuery<ParseObject> classQuery = new ParseQuery<ParseObject>("Paper");
                //paper code

                //Change Prim Val For Text Views
                EndTime = getIntent().getStringExtra("start_time_class");
                StartTime = getIntent().getStringExtra("start_time_class");
                Title = ob.get("Title").toString();
                Date = ob.get("Date").toString();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ob != null) {
            Log.i("title", Title + "");

            TextView titleTv = (TextView) findViewById(R.id.detail_title);
            titleTv.setText(Title);

            TextView dateTv = (TextView) findViewById(R.id.date_text);
            dateTv.setText(Date);


            TextView StartTimeTv = (TextView) findViewById(R.id.start_time_text);
            StartTimeTv.setText(StartTime);

            TextView EndTimeTv = (TextView) findViewById(R.id.end_time_text);
            EndTimeTv.setText(EndTime);

            TextView ReminderTv = (TextView) findViewById(R.id.reminder_text);
            TextView NoteTv = (TextView) findViewById(R.id.detail_title);

            EditText noteEv = (EditText) findViewById(R.id.details_note);
            noteEv.setText(Note);

            FloatingActionButton editFAB = (FloatingActionButton) findViewById(R.id.details_activity_edit_button);
            editFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View detailsView = findViewById(R.id.details_view);
                    detailsView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
