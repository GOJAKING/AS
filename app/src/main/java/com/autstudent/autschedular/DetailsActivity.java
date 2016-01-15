package com.autstudent.autschedular;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.autstudent.autschedular.Helper.DatabaseTitle;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private ParseObject ob;

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
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseTitle.TABLESCHEDULENAME);
            query.whereEqualTo("idRef", idRef);
            ob = query.find().get(0);



            //Retrieving data primitives from object

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ob != null) {
            TextView titleTv = (TextView) findViewById(R.id.detail_title);
            titleTv.setText(ob.getString("Title"));

            TextView dateTv = (TextView) findViewById(R.id.date_text);
            dateTv.setText(ob.getString("Date"));


            TextView StartTimeTv = (TextView) findViewById(R.id.start_time_text);
            StartTimeTv.setText(ob.getString("StartTime"));

            TextView EndTimeTv = (TextView) findViewById(R.id.end_time_text);
            EndTimeTv.setText(ob.getString("EndTime"));

            TextView ReminderTv = (TextView) findViewById(R.id.reminder_text);
            TextView NoteTv = (TextView) findViewById(R.id.detail_title);

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
