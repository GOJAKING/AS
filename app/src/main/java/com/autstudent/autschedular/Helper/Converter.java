package com.autstudent.autschedular.Helper;

import java.util.Calendar;

/**
 * Created by wilzo on 1/01/2016.
 */
public class Converter {
    public static String getDate(int dayCode){
        String day ="Sunday";
        switch (dayCode){
            case Calendar.MONDAY:
                day = "Monday";
                break;
            case Calendar.TUESDAY:
                day = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                day = "Wednesday";
                break;
            case Calendar.THURSDAY:
                day = "Thursday";
                break;
            case Calendar.FRIDAY:
                day = "Friday";
                break;
            case Calendar.SATURDAY:
                day = "Saturday";
                break;
            case Calendar.SUNDAY:
                day = "Sunday";
                break;
        }
        return day;
    }

    public String convertToClock(String input){
        String result ="";
        int counter = 0;
            for(char a: input.toCharArray()){
                if(counter==2){
                    result += ":";
                }
                else {
                    result += a;
                    ++counter;
                }
            }
        return result;
    }
}
