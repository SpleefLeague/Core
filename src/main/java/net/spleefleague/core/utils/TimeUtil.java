/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jonas
 */
public class TimeUtil {
    
    public static String getFormatted(Duration d) {
        String s = "";
        boolean started = false;
        if(started || d.toDays() > 0) {
            started = true;
            s += d.toDays() + " day" + ((d.toDays() > 1) ? "s" : "") + ", ";
            d = d.minus(Duration.ofDays(d.toDays()));
        }
        if(started || d.toHours()> 0) {
            s += d.toHours() + " hour" + ((d.toHours() > 1) ? "s" : "") + ", ";
            started = true;
            d = d.minus(Duration.ofHours(d.toHours()));
        }
        if(started || d.toMinutes()> 0) {
            s += d.toMinutes() + " minute" + ((d.toMinutes() > 1) ? "s" : "") + " and ";
            started = true;
            d = d.minus(Duration.ofMinutes(d.toMinutes()));
        }
        if(started || d.getSeconds()> 0) {
            s += d.getSeconds() + " second" + ((d.getSeconds() > 1) ? "s" : "");
        }
        return s;
    }
    
    public static Duration parseDurationString(String time) {

        Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
        Matcher matcher = timePattern.matcher(time);

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        
        boolean hasFound = false;
        
        while (matcher.find()) {

            if ((matcher.group() == null) || (matcher.group().isEmpty())) {
                continue;
            }

            for (int i = 0; i < matcher.groupCount(); i++) {
                if ((matcher.group(i) == null) || (matcher.group(i).isEmpty())) {
                    continue;
                }
                hasFound = true;
                break;
            }

            if (!hasFound) {
                continue;
            }
            if ((matcher.group(1) != null) && (!matcher.group(1).isEmpty())) {
                years = Integer.parseInt(matcher.group(1));
            }
            if ((matcher.group(2) != null) && (!matcher.group(2).isEmpty())) {
                months = Integer.parseInt(matcher.group(2));
            }
            if ((matcher.group(3) != null) && (!matcher.group(3).isEmpty())) {
                weeks = Integer.parseInt(matcher.group(3));
            }
            if ((matcher.group(4) != null) && (!matcher.group(4).isEmpty())) {
                days = Integer.parseInt(matcher.group(4));
            }
            if ((matcher.group(5) != null) && (!matcher.group(5).isEmpty())) {
                hours = Integer.parseInt(matcher.group(5));
            }
            if ((matcher.group(6) != null) && (!matcher.group(6).isEmpty())) {
                minutes = Integer.parseInt(matcher.group(6));
            }
            if ((matcher.group(7) == null) || (matcher.group(7).isEmpty())) {
                break;
            }
            seconds = Integer.parseInt(matcher.group(7));
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (years > 0) {
            calendar.add(1, years);
        }
        if (months > 0) {
            calendar.add(2, months);
        }
        if (weeks > 0) {
            calendar.add(3, weeks);
        }
        if (days > 0) {
            calendar.add(5, days);
        }
        if (hours > 0) {
            calendar.add(11, hours);
        }
        if (minutes > 0) {
            calendar.add(12, minutes);
        }
        if (seconds > 0) {
            calendar.add(13, seconds);
        }
        
        return Duration.between(Instant.now(), calendar.toInstant());
    }
}
