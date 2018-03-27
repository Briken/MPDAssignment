package mpdproject.gcu.me.org.assignmenttest1;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * David Hesketh Mobile Platform Development Matric No:S1437170
 */

public class Item {
    private Context context;

    public Item(Context context) {
        this.context = context;
        red = context.getResources().getColor(R.color.red);
        yellow = context.getResources().getColor(R.color.yellow);
        green = context.getResources().getColor(R.color.green);
    }

    public String title, description, link, geoRssPoint;
    public String GetTitle()
    {
        return title;
    }
    public String GetDescription()
    {
        return description;
    }
    public String GetLink()
    {
        return link;
    }
    public String start = "Start Date: ";
    public String end = "End Date: ";
    public Date startDate;
    public Date endDate;
    public Long workDuration;

    public int activeColour;
    public int red;
    public int yellow;
    public int green;

    String inputFormat = "EEEE, dd MMMM yyyy";
    String inputDate;
    String convertedFormat = "ddMMyy";
    String[] days;
    String[] months;
    String outDate;




    Date currentDate;
    int yearInt = Calendar.getInstance().get(Calendar.YEAR);


    public void CalculateDate(String startEnd) {
        months = new String[12];
        months[0] = "January";
        months[1] = "February";
        months[2] = "March";
        months[3] = "April";
        months[4] = "May";
        months[5] = "June";
        months[6] = "July";
        months[7] = "August";
        months[8] = "September";
        months[9] = "October";
        months[10] = "November";
        months[11] = "December";
        days = new String[7];
        days[0] = "Monday";
        days[1] = "Tuesday";
        days[2] = "Wednesday";
        days[3] = "Thursday";
        days[4] = "Friday";
        days[5] = "Saturday";
        days[6] = "Sunday";
        for (Integer i = 0; i < days.length; i++) {
            if (description.contains(startEnd + days[i])) {
                inputDate = days[i];
                for (Integer j = 0; j < 32; j++) {
                    if (description.contains(inputDate + ", 0" + j.toString() + " ")) {
                        if (j < 10) {
                            inputDate = inputDate + ", 0" + j.toString() + " ";
                            for (Integer k = 0; k < months.length; k++) {
                                if (description.contains(inputDate + months[k])) {
                                    inputDate = inputDate + months[k] + " ";
                                    for (Integer l = 0; l < 5; l++) {
                                        Integer yrInt = yearInt + l;
                                        String yearStr = yrInt.toString();

                                        if (description.contains(inputDate + yearStr)) ;
                                        {
                                            inputDate = inputDate + yearStr;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    } else if (description.contains(inputDate + ", " + j.toString() + " ")) {
                        inputDate = inputDate + ", " + j.toString() + " ";
                        for (Integer k = 0; k < months.length; k++) {
                            if (description.contains(inputDate + months[k])) {
                                inputDate = inputDate + months[k] + " ";
                                for (Integer l = 0; l < 5; l++) {
                                    Integer yrInt = yearInt + l;
                                    String yearStr = yrInt.toString();

                                    if (description.contains(inputDate + yearStr)) ;
                                    {
                                        inputDate = inputDate + yearStr;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }

        try
        {
            if (inputDate != null)
            {
                DateFormat fromFormatter = new SimpleDateFormat(inputFormat);
                Date date = (Date) fromFormatter.parse(inputDate);

                DateFormat toFormatter = new SimpleDateFormat(convertedFormat);
                outDate = toFormatter.format(date);


                if (startEnd == start) {
                    startDate = toFormatter.parse(outDate);
                }
                if (startEnd == end) {
                    endDate = toFormatter.parse(outDate);
                    WorkDurationCal();
                }
                description = description.replace("<br />", "\n");
                if (description.contains("Traffic Management:"))
                {
                    description = description.replace("Traffic Management:", "\n" +"Traffic Management: ");
                }
                if (description.contains("Diversion Information:"))
                {
                    description = description.replace("Diversion Information:", "\n" + "Diversion Information: ");
                }
            }
            if (title.contains("Closure"))
            {
                activeColour = red;
            }
            if (title.contains("Queue"))
            {
                activeColour = green;
            }
            if (title.contains("Accident") || title.contains("Hazard"))
            {
                activeColour = yellow;
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    public void WorkDurationCal() {
        if (startDate != null || endDate != null)
        {
            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            Log.d("ad", "WorkDurationCal: ");
            workDuration = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            Log.d("tg", workDuration.toString());
        }
        for (Integer i = 0; i < 7; i ++)
        {
            for (Integer j = 0; j < 11; j++)
            {
                if (description.contains(i.toString()+j.toString()+" minutes"))
                {
                    int delay = (i*10) + j;
                    if (delay <= 15)
                    {
                        activeColour = green;
                    }
                    if (delay > 15 && delay <= 45)
                    {
                        activeColour = yellow;
                    }
                    if (delay > 45)
                    {
                        activeColour = green;
                    }
                }
            }
        }

        if (workDuration <= 7)
        {
            activeColour = green;
        }
        if (workDuration > 7 && workDuration <= 14)
        {
            activeColour = yellow;
        }
        if (workDuration > 14)
        {
            activeColour = red;
        }
    }
}
