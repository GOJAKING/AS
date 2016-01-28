package com.autstudent.autschedular;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

    private List<WeekViewEvent> events;

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

        //list of events
        events = new ArrayList<>();
        GetCalendarResources gt = new GetCalendarResources();
        gt.execute();

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

        setUp();

        mWeekView.setShowNowLine(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
    }

    //use this when enter the program
    public void setUp() {
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
            super.onBackPressed();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        switch (id) {
            case R.id.refresh:
                events = new ArrayList<>();
                GetCalendarResources gt = new GetCalendarResources();
                gt.execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_today:
                mWeekView.goToToday();
                break;
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
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics()));
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                SimpleDateFormat format = new SimpleDateFormat(" d/M", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                if (hour == 24) hour = 0;
                if (hour == 0) hour = 0;
                return hour + ":00";
            }
        });
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<>();
        for (WeekViewEvent we : this.events) {
            if (checkEvent(we, newYear, newMonth)) {
                events.add(we);
            }
        }
        return events;
    }

    private boolean checkEvent(WeekViewEvent event, int newYear, int newMonth) {
        return (event.getStartTime().get(Calendar.MONTH) == newMonth - 1) && (event.getStartTime().get(Calendar.YEAR) == newYear)
                && (event.getEndTime().get(Calendar.MONTH) == newMonth - 1) && (event.getEndTime().get(Calendar.YEAR) == newYear);
    }

    private String getEventTitle(Calendar time, String title) {
        return String.format(title + "\n%02d:%02d\n%s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("id_ref", (int) event.getId());
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        intent.putExtra("start_time", timeFormatter.format(event.getStartTime().getTime()));
        intent.putExtra("end_time", timeFormatter.format(event.getEndTime().getTime()));

        startActivity(intent);

        Toast.makeText(MainActivity.this, "Clicked " + event.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        final WeekViewEvent deleteEvent = event;
        final String items[] = {"Delete", "Cancel"};
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setTitle("Options");
        ab.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int choice) {
                if (choice == 0) {
                    ParseUser user = ParseUser.getCurrentUser();
                    if (event.getId() < 10000) {
                        try {
                            ParseRelation<ParseObject> classRelation = user.getRelation("Class");
                            ParseQuery<ParseObject> query = classRelation.getQuery();
                            query.whereEqualTo("ClassID", event.getId() + "");
                            ParseObject ob = query.find().get(0);
                            classRelation.remove(ob);
                            user.save();
                            events = new ArrayList<>();
                            GetCalendarResources gt = new GetCalendarResources();
                            gt.execute();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            ParseRelation<ParseObject> classRelation = user.getRelation("Schedule");
                            ParseQuery<ParseObject> query = classRelation.getQuery();
                            Log.i("id", event.getId() + "");
                            query.whereEqualTo("idRef", event.getId());
                            List<ParseObject> list = query.find();
                            Log.i("size", list.size() + "");
                            ParseObject ob = list.get(0);
                            classRelation.remove(ob);
                            user.save();
                            ob.delete();
                            events = new ArrayList<>();
                            GetCalendarResources gt = new GetCalendarResources();
                            gt.execute();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        ab.show();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Refreshing Timetable");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    private class GetCalendarResources extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ParseUser parseUser = ParseUser.getCurrentUser();
            ParseRelation<ParseObject> scheduleRelation = parseUser.getRelation("Schedule");
            ParseQuery<ParseObject> query = scheduleRelation.getQuery();
            List<ParseObject> schedule = new ArrayList<>();
            try {
                schedule = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // Populate the week view with some events.
            for (ParseObject ob : schedule) {
                String[] start = ob.get("StartTime").toString().split(":");
                Calendar startCalendar = Calendar.getInstance();
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    Date date = format.parse(ob.get("StartDate").toString());
                    startCalendar.setTime(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int startMonth = startCalendar.get(Calendar.MONTH);
                int startYear = startCalendar.get(Calendar.YEAR);
                String title = ob.get("Title").toString();
                int id = Integer.parseInt(ob.get("idRef").toString());

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
                startTime.set(Calendar.MINUTE, Integer.parseInt(start[1]));
                startTime.set(Calendar.DAY_OF_MONTH, startCalendar.get(Calendar.DAY_OF_MONTH));
                startTime.set(Calendar.MONTH, startMonth);
                startTime.set(Calendar.YEAR, startYear);

                Calendar endCalendar = Calendar.getInstance();
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    Date date = format.parse(ob.get("EndDate").toString());
                    endCalendar.setTime(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int endMonth = endCalendar.get(Calendar.MONTH);
                int endYear = endCalendar.get(Calendar.YEAR);

                String[] end = ob.get("EndTime").toString().split(":");
                Calendar endTime = Calendar.getInstance();
                endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end[0]));
                endTime.set(Calendar.MINUTE, Integer.parseInt(end[1]));
                endTime.set(Calendar.DAY_OF_MONTH, endCalendar.get(Calendar.DAY_OF_MONTH));
                endTime.set(Calendar.MONTH, endMonth);
                endTime.set(Calendar.YEAR, endYear);

                WeekViewEvent event = new WeekViewEvent(id, getEventTitle(startTime, title), startTime, endTime);
                int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                event.setColor(randomAndroidColor);
                events.add(event);
            }

            ParseRelation<ParseObject> classRelation = parseUser.getRelation("Class");
            ParseQuery<ParseObject> classQuery = classRelation.getQuery();
            List<ParseObject> classes = new ArrayList<>();
            try {
                classes = classQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (ParseObject ob : classes) {
                String title = "";
                ParseQuery<ParseObject> paperQuery = new ParseQuery<ParseObject>("paper");
                paperQuery.whereEqualTo("objectId", ob.getParseObject("paper").getObjectId());
                try {
                    title = paperQuery.find().get(0).get("paper_title").toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ParseRelation<ParseObject> streamRelation = ob.getRelation("stream");
                ParseQuery streamQuery = streamRelation.getQuery();
                List<ParseObject> streams = new ArrayList<>();
                try {
                    streams = streamQuery.find();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int[] sem1 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
                if (Integer.parseInt(ob.get("semester").toString()) == 1) {
                    sem1 = new int[]{0, 1, 2, 3, 4, 5};
                } else {
                    sem1 = new int[]{6, 7, 8, 9, 10, 11};
                }
                for (ParseObject stream : streams) {
                    for (int month : sem1) {
                        int week = 1;
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.MONTH, month);
                        time.set(Calendar.YEAR, Integer.parseInt(ob.get("year").toString()));
                        time.set(Calendar.DAY_OF_MONTH, 1);
                        int day = time.getTime().getDay();
                        // if it is not monday or sunday or saturday make it week 2 or else overlap between week 1 and week 5 of the month
                        if (!(day == 1 || day == 0 || day == 6)) {
                            week = 2;
                        }
                        for (; week < 6; ++week) {
                            String[] start = stream.get("start").toString().split(":");
                            int id = Integer.parseInt(ob.get("ClassID").toString());
                            Calendar startTime = Calendar.getInstance();
                            startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(start[0]));
                            startTime.set(Calendar.MINUTE, Integer.parseInt(start[1]));
                            startTime.set(Calendar.MONTH, month);
                            startTime.set(Calendar.YEAR, Integer.parseInt(ob.get("year").toString()));
                            startTime.set(Calendar.DAY_OF_WEEK, Integer.parseInt(stream.get("day").toString()));
                            startTime.set(Calendar.WEEK_OF_MONTH, week);
                            String[] end = stream.get("end").toString().split(":");
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
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWeekView.notifyDatasetChanged();
            hideProgressDialog();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mWeekViewType = TYPE_WEEK_VIEW;
                mWeekView.setNumberOfVisibleDays(7);
                Calendar c = Calendar.getInstance();
                mWeekView.goToHour(c.get(Calendar.HOUR_OF_DAY));

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, getResources().getDisplayMetrics()));
            }
        }
    }
}

