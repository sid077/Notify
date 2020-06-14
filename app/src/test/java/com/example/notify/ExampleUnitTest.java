package com.example.notify;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
       // assertEquals(4, 2 + 2);

    }

    @Test

    public void isSleepTime() throws ParseException {

        String from = "04:02 PM";

        String to = "07:12 PM";
        Date isDate  = Date.from(Instant.EPOCH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date dateFrom ;
        dateFormat.format(isDate);
        dateFrom =  dateFormat.parse(from);
        Date dateTo ;
        dateTo= dateFormat.parse(to);

        Calendar calendar = Calendar.getInstance();
        calendar.set(1970,0,01);
        calendar.set(Calendar.DAY_OF_MONTH,01);
        calendar.set(Calendar.DAY_OF_WEEK,5);
        Date d = calendar.getTime();
         d.setDate(1);
                    long tol = dateTo.getTime();
        long froml = dateFrom.getTime();
       long isl = d.getTime();

       int x=0;

    }

}