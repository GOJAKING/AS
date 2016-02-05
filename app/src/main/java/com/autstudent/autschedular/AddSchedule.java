package com.autstudent.autschedular;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.autstudent.autschedular.Helper.DatabaseTitle;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wilzo on 3/01/2016.
 */
public class AddSchedule extends AddActivity.PlaceholderFragment {

    private TextView titleTV;
    private TextView startDateTV;
    private TextView endDateTV;
    private TextView startTimeTV;
    private TextView endTimeTV;
    private TextView reminderTV;
    private TextView noteTV;
    private EditText EditTitle;
    private EditText EditTextNote;


    private String title;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String reminder;
    private String notes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_schedule_add, container, false);
        titleTV = (TextView) rootView.findViewById(R.id.title_text);
        startDateTV = (TextView) rootView.findViewById(R.id.start_date_text);
        endDateTV = (TextView) rootView.findViewById(R.id.end_date_text);
        startTimeTV = (TextView) rootView.findViewById(R.id.start_time_text);
        endTimeTV = (TextView) rootView.findViewById(R.id.end_time_text);
        reminderTV = (TextView) rootView.findViewById(R.id.reminder_text);
        EditTextNote = (EditText) rootView.findViewById(R.id.add_note_edit_text);
        EditTitle = (EditText) rootView.findViewById(R.id.title_edit_text);
        noteTV = (TextView) rootView.findViewById(R.id.note_text);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        Calendar current = Calendar.getInstance();
        Date now = new Date();
        current.setTime(now);

        startDateTV.setText(dateFormatter.format(now));
        endDateTV.setText(dateFormatter.format(now));
        startTimeTV.setText(timeFormatter.format(now));
        now.setHours(now.getHours() + 1);
        endTimeTV.setText(timeFormatter.format(now));

        Button button = (Button) rootView.findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EditTitle.getText().length()==0) {
                    title = titleTV.getText().toString();
                }
                else {
                    title = EditTitle.getText().toString();
                }
                startDate = startDateTV.getText().toString();
                endDate = endDateTV.getText().toString();
                startTime =  startTimeTV.getText().toString();
                endTime = endTimeTV.getText().toString();
                reminder = reminderTV.getText().toString();
                notes = EditTextNote.getText().toString();
                SavingInfo si = new SavingInfo();
                si.execute();
            }
        });

        setOnClickDatePicker(startDateTV);
        setOnClickDatePicker(endDateTV);
        setOnClickTimePickerEditor(startTimeTV, endTimeTV);
        setOnClickReminderPicker(reminderTV, rootView);
        setOnClickNote(noteTV);
        setOnTitleNote(titleTV);

        return rootView;
    }

    private class SavingInfo extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "",
                    "Saving", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseObject ob = new ParseObject(DatabaseTitle.TABLESCHEDULENAME);
            ob.put("Title", title);
            ob.put("StartDate", startDate);
            ob.put("EndDate", endDate);
            ob.put("StartTime", startTime);
            ob.put("EndTime", endTime);
            ob.put("Reminder", reminder);
            ob.put("Note", notes);
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DatabaseTitle.TABLESCHEDULENAME);
            query.addDescendingOrder("idRef");
            try {
                List<ParseObject> idLists = query.find();
                if (idLists.isEmpty()) {
                    ob.put("idRef", 10000);
                } else {
                    String prevID = idLists.get(0).get("idRef").toString();
                    ob.put("idRef", (Integer.parseInt(prevID) + 1));
                }
                ob.save();
                ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<ParseObject> relation = user.getRelation("Schedule");
                relation.add(ob);
                user.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(pd.isShowing())
            pd.dismiss();
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    public void setOnTitleNote(final TextView editNote) {
        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTitle.setVisibility(View.VISIBLE);
                titleTV.setVisibility(View.GONE);
            }
        });
    }

    public void setOnClickNote(final TextView editNote) {
        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextNote.setVisibility(View.VISIBLE);
                noteTV.setVisibility(View.GONE);
            }
        });
    }

    public void setOnClickDatePicker(final TextView editDate) {
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        editDate.setText(dateFormatter.format(newDate.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });
    }

    public void setOnClickTimePickerEditor(TextView editStartTime, final TextView editEndTime) {
        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHourString = selectedHour + "";
                        if (selectedHourString.length() == 1) {
                            selectedHourString = "0" + selectedHourString;
                        }
                        String selectedMinuteString = selectedMinute + "";
                        if (selectedMinuteString.length() == 1) {
                            selectedMinuteString = "0" + selectedMinuteString;
                        }
                        startTimeTV.setText(selectedHourString + ":" + selectedMinuteString);
                        String endSelectedHourString = (selectedHour+1) + "";
                        if (endSelectedHourString.length() == 1) {
                            endSelectedHourString = "0" + endSelectedHourString;
                        }
                        endTimeTV.setText(endSelectedHourString + ":" + selectedMinuteString);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String selectedHourString = selectedHour + "";
                        if (selectedHourString.length() == 1) {
                            selectedHourString = "0" + selectedHourString;
                        }
                        String selectedMinuteString = selectedMinute + "";
                        if (selectedMinuteString.length() == 1) {
                            selectedMinuteString = "0" + selectedMinuteString;
                        }
                        endTimeTV.setText(selectedHourString + ":" + selectedMinuteString);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    public void setOnClickReminderPicker(TextView editReminder, View view) {
        editReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Reminder Time");

                final TextView minuteText = new TextView(getActivity());
                minuteText.setText(0 + "");

                final TextView textProgress = new TextView(getActivity());
                textProgress.setText("Remind before : 0 min");

                final SeekBar input = new SeekBar(getActivity());
                input.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChanged = 0;
                    int minute = 0;

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChanged = progress;
                        double calc = (60 * (progress / 100.00));
                        minute = (int) calc;
                        textProgress.setText("Remind before : " + minute + " min");
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Toast.makeText(getActivity(), "seek bar progress:" + progressChanged,
                                Toast.LENGTH_SHORT).show();
                        minuteText.setText("" + minute);
                    }
                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                textProgress.setLayoutParams(lp);
                input.setLayoutParams(lp);
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(textProgress);
                ll.addView(input);
                alertDialog.setView(ll);
                alertDialog.setPositiveButton("submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                reminderTV.setText(minuteText.getText());
                            }
                        });

                alertDialog.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });
    }

}


