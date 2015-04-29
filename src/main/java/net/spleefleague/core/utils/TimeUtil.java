/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import java.time.Duration;
import java.time.Period;
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
            s += d.toDays() + " day" + ((d.toDays() != 1) ? "s" : "") + ", ";
            d = d.minus(Duration.ofDays(d.toDays()));
        }
        if(started || d.toHours()> 0) {
            s += d.toHours() + " hour" + ((d.toHours() != 1) ? "s" : "") + ", ";
            started = true;
            d = d.minus(Duration.ofHours(d.toHours()));
        }
        if(started || d.toMinutes()> 0) {
            s += d.toMinutes() + " minute" + ((d.toMinutes() != 1) ? "s" : "") + " and ";
            started = true;
            d = d.minus(Duration.ofMinutes(d.toMinutes()));
        }
        if(started || d.getSeconds()> 0) {
            s += d.getSeconds() + " second" + ((d.getSeconds() != 1) ? "s" : "");
        }
        return s;
    }
    
    public static Duration parseDurationString(String time) {

        Pattern timePattern = Pattern.compile("[1-9][0-9]*(y|mo|w|h|d|m|s)");
        Matcher matcher = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        
        while (matcher.find()) {
            String r = matcher.group();
            switch(r.charAt(r.length() - 1)) {
                case 'y': {
                    years = Integer.parseInt(r.replace("y", ""));
                    break;
                }
                case 'o': {
                    months = Integer.parseInt(r.replace("mo", ""));
                    break;
                }
                case 'w': {
                    weeks = Integer.parseInt(r.replace("y", ""));
                    break;
                }
                case 'd': {
                    days = Integer.parseInt(r.replace("d", ""));
                    break;
                }
                case 'h': {
                    hours = Integer.parseInt(r.replace("h", ""));
                    break;
                }
                case 'm': {
                    minutes = Integer.parseInt(r.replace("m", ""));
                    break;
                }
                case 's': {
                    seconds = Integer.parseInt(r.replace("s", ""));
                    break;
                }
                
            }
        }
        return Duration.ofDays(Period.ofYears(years).plusMonths(months).plusDays(weeks * 7).plusDays(days).getDays()).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }
}
