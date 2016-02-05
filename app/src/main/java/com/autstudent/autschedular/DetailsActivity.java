package com.autstudent.autschedular;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.autstudent.autschedular.Helper.DatabaseTitle;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {
    private ParseObject ob;

    //Strings for Details Activity
    private String title = "";
    private String subtitle = "paper code : ";
    private String startTime = "";
    private String endTime = "";
    private String note = "";
    private String startDate = "";
    private String endDate = "";
    private String reminder = "";

    FloatingActionButton Fab;

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

        Fab = (FloatingActionButton) findViewById(R.id.details_edit_button);


        int idRef = getIntent().getIntExtra("id_ref", -1);
        try {
            if (idRef >= 10000) {
                //checking for if is schedule event
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseTitle.TABLESCHEDULENAME);
                query.whereEqualTo("idRef", idRef);
                ob = query.find().get(0);

                //Change Prim Val For Text Views
                note = ob.get("Note").toString();
                endTime = ob.get("EndTime").toString();
                startTime = ob.get("StartTime").toString();
                title = ob.get("Title").toString();

                //setting start date to MON 29 FEB Format

                startDate = ob.get("StartDate").toString();
                    String[] startDateParts = startDate.split("-");
                    Date formattedStartDate = new Date();
                    formattedStartDate.setDate(Integer.parseInt(startDateParts[0]));
                    formattedStartDate.setMonth(Integer.parseInt(startDateParts[1]));
                    formattedStartDate.setYear(Integer.parseInt(startDateParts[2]));

                SimpleDateFormat startSDF = new SimpleDateFormat("dd-MM-yyyy");
                startSDF.format(formattedStartDate);
                startDate = formattedStartDate + "";
                startDateParts = startDate.split(" ");
                startDate = startDateParts[0].toUpperCase() + " " +  startDateParts[1].toUpperCase()
                        + " " +  startDateParts[2].toUpperCase();

                //setting end date to MON 29 FEB Format

                endDate = ob.get("EndDate").toString();
                String[] endDateParts = endDate.split("-");
                    Date formattedEndDate = new Date();
                    formattedEndDate.setDate(Integer.parseInt(endDateParts[0]));
                    formattedEndDate.setMonth(Integer.parseInt(endDateParts[1]));
                    formattedEndDate.setYear(Integer.parseInt(endDateParts[2]));

                SimpleDateFormat endSDF = new SimpleDateFormat("dd-MM-yyyy");
                endSDF.format(formattedEndDate);
                endDate = formattedEndDate + "";
                endDateParts = endDate.split(" ");
                endDate = endDateParts[0].toUpperCase() + " " +  endDateParts[1].toUpperCase()
                        + " " +  endDateParts[2].toUpperCase();

                reminder = ob.get("Reminder").toString();
                subtitle = ""; //for classes only
                if(reminder.equals("") || reminder.equals(null)) reminder = "0";


            } else {
                //checking for if is class event
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("class");
                query.whereEqualTo("ClassID", idRef+"");
                ob = query.find().get(0);

                ParseObject paper = ob.getParseObject("paper");
                title = paper.get("paper_title").toString();
                subtitle += paper.get("paper_code").toString();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ob != null) {
            Log.i("title", title + "");


            getSupportActionBar().setTitle(title);

//            TextView subTitleTv = (TextView) findViewById(R.id.detail_subtitle);
//            subTitleTv.setText(subtitle);

            TextView startDateTv = (TextView) findViewById(R.id.startDate_text);
            startDateTv.setText(startDate);

            TextView endDateTv = (TextView) findViewById(R.id.toEndDate_text);
            endDateTv.setText("  TO  " + endDate);

            TextView StartTimeTv = (TextView) findViewById(R.id.start_time_text);
            StartTimeTv.setText(startTime);

            TextView EndTimeTv = (TextView) findViewById(R.id.end_time_text);
            EndTimeTv.setText(endTime);

            TextView ReminderTv = (TextView) findViewById(R.id.reminder_text);
            ReminderTv.setText(reminder);

            //TextView NoteTv = (TextView) findViewById(R.id.detail_title);

            EditText noteEv = (EditText) findViewById(R.id.details_note);
            noteEv.setText(note);

            if(endDate.equals(startDate))
            {
                endDateTv.setVisibility(TextView.GONE);
            }

            FloatingActionButton editFAB = (FloatingActionButton) findViewById(R.id.details_edit_button);
            editFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View detailsView = findViewById(R.id.details_view);
                    View editView = findViewById(R.id.edit_view);
                    detailsView.setVisibility(View.GONE);
                    editView.setVisibility(View.VISIBLE);


                }
            });
        }




        /**
         * Edit View setting buttons and textviews
         */

        Button startDateButton = (Button) findViewById(R.id.editDateStart);
        Button endDateButton = (Button) findViewById(R.id.editDateEnd);
        Button startTimeButton = (Button) findViewById(R.id.start_time_text_editver);
        Button endTimeButton = (Button) findViewById(R.id.end_time_text_editver);
        Button reminderButton = (Button) findViewById(R.id.reminder_message_editver);
        EditText editNote = (EditText) findViewById(R.id.details_note_editver);


        startDateButton.setText(startDate);
        endDateButton.setText(endDate);
        startTimeButton.setText(startTime);
        endTimeButton.setText(endTime);
        reminderButton.setText(reminder + " Minutes before event");
        editNote.setText(note);

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
