package com.xsp.pda.main;

import java.util.Date;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2010
 * http://www.tomclaw.com/
 * @author Игорь
 */
public class TimeUtil {
    /*/////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                 METHODS FOR DATE AND TIME PROCESSING                  //
    //                                                                       //
    /////////////////////////////////////////////////////////////////////////*/

    private final static String error_str = "[incorrect data]";
    final public static int TIME_SECOND = 0;
    final public static int TIME_MINUTE = 1;
    final public static int TIME_HOUR = 2;
    final public static int TIME_DAY = 3;
    final public static int TIME_MON = 4;
    final public static int TIME_YEAR = 5;
    final private static byte[] dayCounts = TransUtil.explodeToBytes(
            "31,28,31,30,31,30,31,31,30,31,30,31", ',', 10);

    /* Creates current date (GMT or local) */
    public static long createCurrentDate(boolean gmt) {
        // getTime() returns GTM time
        long result = new Date().getTime() / 1000;

        /* convert result to GMT time */
        long diff = MidletMain.timeOffset;
        result += (diff * 3600);

        /* returns GMT or local time */
        return gmt ? result : gmtTimeToLocalTime(result);
    }

    /* Show date string */
    public static String getDateString(boolean onlyTime, long date) {
        if (date == 0) {
            return error_str;
        }

        int[] loclaDate = createDate(date);

        StringBuffer sb = new StringBuffer();

        if (!onlyTime) {
            sb.append(makeTwo(loclaDate[TIME_DAY])).append('.').append(
                    makeTwo(loclaDate[TIME_MON])).append('.').append(
                    loclaDate[TIME_YEAR]).append(' ');
        }

        sb.append(makeTwo(loclaDate[TIME_HOUR])).append(':').append(
                makeTwo(loclaDate[TIME_MINUTE]));

        return sb.toString();
    }

    /* Show time string */
    public static String getTimeString(long date) {
        if (date == 0) {
            return error_str;
        }

        int[] loclaDate = createDate(date);

        StringBuffer sb = new StringBuffer();

        sb.append(makeTwo(loclaDate[TIME_HOUR])).append(':').append(
                makeTwo(loclaDate[TIME_MINUTE])).append(':').append(makeTwo(loclaDate[TIME_SECOND]));

        return sb.toString();
    }

    /* Generates seconds count from 1st Jan 1970 till mentioned date */
    public static long createLongTime(int year, int mon, int day, int hour,
            int min, int sec) {
        int day_count, i, febCount;

        day_count = (year - 1970) * 365 + day;
        day_count += (year - 1968) / 4;
        if (year >= 2000) {
            day_count--;
        }

        if ((year % 4 == 0) && (year != 2000)) {
            day_count--;
            febCount = 29;
        } else {
            febCount = 28;
        }

        for (i = 0; i < mon - 1; i++) {
            day_count += (i == 1) ? febCount : dayCounts[i];
        }

        return day_count * 24L * 3600L + hour * 3600L + min * 60L + sec;
    }

    // Creates array of calendar values form value of seconds since 1st jan 1970 (GMT)
    public static int[] createDate(long value) {
        int total_days, last_days, i;
        int sec, min, hour, day, mon, year;

        sec = (int) (value % 60);

        min = (int) ((value / 60) % 60); // min
        value -= 60 * min;

        hour = (int) ((value / 3600) % 24); // hour
        value -= 3600 * hour;

        total_days = (int) (value / (3600 * 24));

        year = 1970;
        for (;;) {
            last_days = total_days - ((year % 4 == 0) && (year != 2000) ? 366 : 365);
            if (last_days <= 0) {
                break;
            }
            total_days = last_days;
            year++;
        } // year

        int febrDays = ((year % 4 == 0) && (year != 2000)) ? 29 : 28;

        mon = 1;
        for (i = 0; i < 12; i++) {
            last_days = total_days - ((i == 1) ? febrDays : dayCounts[i]);
            if (last_days <= 0) {
                break;
            }
            mon++;
            total_days = last_days;
        } // mon

        day = total_days; // day

        return new int[]{sec, min, hour, day, mon, year};
    }

    public static String getDateString(boolean onlyTime) {
        return getDateString(onlyTime, createCurrentDate(false));
    }

    public static String getTimeString() {
        return getTimeString(createCurrentDate(false));
    }

    public static long gmtTimeToLocalTime(long gmtTime) {
        return gmtTime + (MidletMain.gmtOffset + MidletMain.dls) * 3600L;
    }

    public static String longitudeToString(long seconds) {
        StringBuffer buf = new StringBuffer();
        int days = (int) (seconds / 86400);
        seconds %= 86400;
        int hours = (int) (seconds / 3600);
        seconds %= 3600;
        //TODO: locale "days", "hours", "minutes" 
        int minutes = (int) (seconds / 60);

        if (days != 0) {
            buf.append(days).append(' ').append(
                    ("days")).append(' ');
        }
        if (hours != 0) {
            buf.append(hours).append(' ').append(
                    ("hours")).append(' ');
        }
        if (minutes != 0) {
            buf.append(minutes).append(' ').append(
                    ("minutes"));
        }

        return buf.toString();
    }

    public static String makeTwo(int number) {
        if (number < 10) {
            return ("0" + String.valueOf(number));
        } else {
            return (String.valueOf(number));
        }
    }
}
