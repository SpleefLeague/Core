/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

/**
 *
 * @author Jonas
 */
public class StringUtil {
    
    public static String fromArgsArray(String[] args, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i <= end; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }
    
    public static String fromArgsArray(String[] args, int start) {
        return fromArgsArray(args, start, args.length - 1);
    }
    
    public static String fromArgsArray(String[] args) {
        return fromArgsArray(args, 0);
    }
    
    public static String upperCaseFirst(String s) {
        s = s.toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
