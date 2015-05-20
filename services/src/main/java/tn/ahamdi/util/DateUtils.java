package tn.ahamdi.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ali
 * Date: 05/05/15
 * Time: 16:56
 */
public class DateUtils {
  private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
    put("^\\d{8}$", "yyyyMMdd");
    put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
    put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
    put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
    put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
    put("^\\d{12}$", "yyyyMMddHHmm");
    put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
    put("^\\d{14}$", "yyyyMMddHHmmss");
    put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
  }};

  /**
   * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
   * format is unknown. You can simply extend DateUtil with more formats if needed.
   *
   * @param dateString The date string to determine the SimpleDateFormat pattern for.
   * @return The matching SimpleDateFormat pattern, or null if format is unknown.
   * @see SimpleDateFormat
   */
  public static String determineDateFormat(String dateString) {
    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
      if (dateString.toLowerCase().matches(regexp)) {
        return DATE_FORMAT_REGEXPS.get(regexp);
      }
    }
    return null; // Unknown format.
  }

  public static Calendar convertDate(String stringToConvert) throws ParseException {
    String pattern = determineDateFormat(stringToConvert);
    if (pattern != null && !pattern.isEmpty()) {
      DateFormat dateFormat = new SimpleDateFormat(pattern);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateFormat.parse(stringToConvert));
      return calendar;
    }
    return null;
  }

  /**
   * Calculates the start of the day of a given date
   * @param date
   * @return Calendar
   */
  public static Calendar getStartOfDay(Date date) {
  //start day
  Calendar dayStart = Calendar.getInstance();
  dayStart.setTime(date);
  dayStart.set(Calendar.HOUR_OF_DAY,0);
  dayStart.set(Calendar.MINUTE,0);
  dayStart.set(Calendar.SECOND,0);
  dayStart.set(Calendar.MILLISECOND,0);
  return dayStart;
}
  public static Calendar getEndOfDay(Date date) {
    //End day
    Calendar dayEnd = Calendar.getInstance();
    dayEnd.setTime(date);
    dayEnd.set(Calendar.HOUR, 23);
    dayEnd.set(Calendar.MINUTE, 59);
    dayEnd.set(Calendar.SECOND, 59);
    dayEnd.set(Calendar.MILLISECOND, 999);
    return dayEnd;
  }

}
