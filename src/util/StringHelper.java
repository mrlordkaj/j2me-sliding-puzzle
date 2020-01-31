/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Thinh
 */
public class StringHelper {
    public static String timeParse(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        StringBuffer rs = new StringBuffer();
        if(min < 10) rs.append("0");
        rs.append(min + ":");
        if(sec < 10) rs.append("0");
        rs.append(Integer.toString(sec));
        return rs.toString();
    }
}
