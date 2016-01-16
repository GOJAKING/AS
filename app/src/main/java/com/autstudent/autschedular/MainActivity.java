package com.autstudent.autschedular;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.autstudent.autschedular.Helper.DatabaseTitle;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MonthLoader.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    private static String userName = "USER";
    private static String emailAddress = "USER@ALIBABA.com";

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    private ProgressDialog mProgressDialog;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ParseUser currentUser = ParseUser.getCurrentUser();
        TextView userNameTV = (TextView) header.findViewById(R.id.username_drawer);
        userNameTV.setText(currentUser.get("display_name").toString());
        TextView emailAddressTV = (TextView) header.findViewById(R.id.email_drawer);
        emailAddressTV.setText(currentUser.getEmail());
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        goToTodayView();
        mWeekView.setShowNowLine(true);
    }

    //use this when enter the program
    public void goToTodayView() {
        mWeekView.goToToday();
        mWeekViewType = TYPE_DAY_VIEW;
        mWeekView.setNumberOfVisibleDays(1);
        Calendar c = Calendar.getInstance();
        mWeekView.goToHour(c.get(Calendar.HOUR_OF_DAY));

        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.add_activity:
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_one:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);
                    Calendar c = Calendar.getInstance();
                    mWeekView.goToHour(c.get(Calendar.HOUR_OF_DAY));

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                break;
            case R.id.nav_week:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);
                    Calendar c = Calendar.getInstance();
                    mWeekView.goToHour(c.get(Calendar.HOUR_OF_DAY));

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                break;
            case R.id.nav_three:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);
                    Calendar c = Calendar.getInstance();
                    mWeekView.goToHour(c.get(Calendar.HOUR_OF_DAY));

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                break;
            case R.id.nav_option:
                Toast.makeText(MainActivity.this, "aylmao theres options", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.putExtra("message", "logout");
                startActivity(intent);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        //current user
        ParseUser parseUser = ParseUser.getCurrentUser();

        // getting the info from schedule
        ParseRelation<ParseObject> relation = parseUser.getRelation("Schedule");
        ParseQuery<ParseObject> query = relation.getQuery();
        List<ParseObject> schedule = new ArrayList<>();
        try {
            schedule = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (ParseObject ob : schedule) {
            String[] start = ob.get("StartTime").toString().split(":");
            Calendar calendar = Calendar.getInstance();
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Date date = format.parse(ob.get("Date").toString());
                calendar.setTime(date);
            } catch (Exception e) {
                e.printStackTrace();
            }


            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            String title = ob.get("Title").toString();
            int id = Integer.parseInt(ob.get("idRef").toString());

            if (newMonth - 1 == month && newYear == year) {
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
                startTime.set(Calendar.MINUTE, Integer.parseInt(start[1]));
                startTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                startTime.set(Calendar.MONTH, month);
                startTime.set(Calendar.YEAR, year);
                String[] end = ob.get("EndTime").toString().split(":");
                Calendar endTime = (Calendar) startTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end[0]));
                endTime.set(Calendar.MINUTE, Integer.parseInt(end[1]));
                WeekViewEvent event = new WeekViewEvent(id, getEventTitle(startTime, title), startTime, endTime);
                int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                event.setColor(randomAndroidColor);
                events.add(event);
            }
        }


        // getting the info from class
        ParseRelation<ParseObject> classRelation = parseUser.getRelation(DatabaseTitle.TABLESCLASSNAME);
        ParseQuery<ParseObject> classQuery = classRelation.getQuery();
        List<ParseObject> classes = new ArrayList<>();
        try {
            classes = classQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (ParseObject ob : classes) {
            ParseRelation<ParseObject> streamRelation = ob.getRelation(DatabaseTitle.TABLESSTREAMNAME);
            ParseQuery<ParseObject> streamQuery = streamRelation.getQuery();
            List<ParseObject> streams = new ArrayList<>();
            try {
                streams = streamQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (ParseObject stream : streams) {
                String[] start = stream.get("start").toString().split(":");
                int day = Integer.parseInt(stream.get("day").toString());
                ParseQuery<ParseObject>paperQuery = new ParseQuery<ParseObject>("paper");
                paperQuery.whereEqualTo("objectId",ob.getParseObject("paper").getObjectId());
                String title = "";
                try {
                    List<ParseObject> paper = paperQuery.find();
                    title = paper.get(0).get("paper_title").toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                int startHour = Integer.parseInt(start[0]);
                int startMinute = Integer.parseInt(start[1]);
                String[] end = stream.get("end").toString().split(":");
                int endHour = Integer.parseInt(end[0]);
                int endMinute =  Integer.parseInt(end[1]);
                for(int i = 0; i<4 ;i++) {
                    int id = Integer.parseInt(ob.get("ClassID").toString());
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY,startHour );
                    startTime.set(Calendar.MINUTE, startMinute);
                    startTime.set(Calendar.DAY_OF_WEEK, day);
                    startTime.set(Calendar.WEEK_OF_MONTH, i);
                    startTime.set(Calendar.MONTH, newMonth);
                    startTime.set(Calendar.YEAR, newYear);
                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, endHour);
                    endTime.set(Calendar.MINUTE,endMinute);
                    WeekViewEvent event = new WeekViewEvent(id, getEventTitle(startTime, title), startTime, endTime);
                    int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                    int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                    event.setColor(randomAndroidColor);
                    events.add(event);
                }

            }
        }

        return events;
    }


    private String getEventTitle(Calendar time, String title) {
        return String.format(title + "\n%02d:%02d\n%s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("id_ref", (int) event.getId());
        startActivity(intent);

        Toast.makeText(MainActivity.this, "Clicked " + event.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(MainActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }
}

