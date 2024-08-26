package com.assignement.realtimequoteviewer.utils;

import com.assignement.realtimequoteviewer.model.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {

    private static final Logger logger = LoggerFactory.getLogger(CalendarUtils.class);

    public static BigDecimal getOptionTimeToExpiryYear(Security security) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date currentDate = calendar.getTime();

        String expiryMonth = security.getTickerId().split("-")[1];
        String expiryYear = security.getTickerId().split("-")[2];
        int month = getMonthValue(expiryMonth);

        if (month == -1) {
            logger.error("Unable to parse expiry Month - Aborting expiryCalculation");
            return null;
        }

        calendar.set(Integer.parseInt(expiryYear), month, 1);

        Date expiryDate = calendar.getTime();

        float expiryInYears = (expiryDate.getTime() - currentDate.getTime()) / (1000f * 60 * 60 * 24 * 365);

        return BigDecimal.valueOf(expiryInYears);

    }

    public static int getMonthValue(String expiryMonth) {
        Date date;
        try {
            date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(expiryMonth);
        } catch (ParseException e) {
            logger.error("UnsupportedMonth Format " + expiryMonth);
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }
}
