package org.devoware.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil {
    
    public static final Pattern IPV4_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");

    public static final Pattern WHITESPACE_COLLAPSE_PATTERN1 = Pattern.compile("([\\r\\n\\t])");
    public static final Pattern WHITESPACE_COLLAPSE_PATTERN2 = Pattern.compile("(\\s{2,})");
    
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    public static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();

    public static final long DEFAULT_START_TIME = parseDate("2011-12-23T00:00:00.000").getTime();
    public static final long DEFAULT_END_TIME = parseDate("2011-12-24T00:00:00.000").getTime();
    
    private static final NumberFormat NF_HOURS = NumberFormat.getIntegerInstance();
    private static final NumberFormat NF_MIN_SECS = NumberFormat.getIntegerInstance();
    private static final NumberFormat NF_MILLIS = NumberFormat.getIntegerInstance();
    
    static {
        NUMBER_FORMAT.setMinimumIntegerDigits(1);
        NUMBER_FORMAT.setMinimumFractionDigits(3);
        NUMBER_FORMAT.setMaximumFractionDigits(3);
        
        NF_HOURS.setMinimumIntegerDigits(1);
        NF_MIN_SECS.setMinimumIntegerDigits(2);
        NF_MILLIS.setMinimumIntegerDigits(3);
    }
    
    private AppUtil () {}
    
    public static String formatDate (long date) {
        return formatDate(new Date(date));
    }
    
    public static String formatDate (Date date) {
        return DATE_FORMAT.format(date);
    }
    
    public static String formatDouble (double number) {
        return NUMBER_FORMAT.format(number);
    }
    
    public static String formatPercent (double number) {
        return PERCENT_FORMAT.format(number);
    }

    public static String formatElapsedTime(long elapsed) {
        long millis = elapsed % 1000;
        elapsed /= 1000;
        long seconds = elapsed % 60;
        elapsed /= 60;
        long minutes = elapsed % 60;
        elapsed /= 60;
        long hours = elapsed;
        return NF_HOURS.format(hours) + ":" + NF_MIN_SECS.format(minutes) + ":" + NF_MIN_SECS.format(seconds) + "." + NF_MILLIS.format(millis);
    }
    
    public static long elapsedTimeToMinutes (long elapsed) {
        long millis = elapsed % 1000;
        elapsed /= 1000;
        long seconds = elapsed % 60;
        if (millis > 500) {
            seconds++;
        }
        elapsed /= 60;
        if (seconds > 30) {
            elapsed++;
        }
        return elapsed;
    }
    
    public static long getRoundedTime (long time) {
        int elapsedIncrements = getElapsedIncrements(DEFAULT_START_TIME, time);
        return DEFAULT_START_TIME + (elapsedIncrements * 5 * 60 * 1000);
    }

    public static int getElapsedIncrements (long from, long to) {
        long elapsedMinutes = elapsedTimeToMinutes(to - from);
        int result = (int) (elapsedMinutes / 5);
        if (elapsedMinutes % 5 > 2) {
            result++;
        }
        //return Math.max(result, 1);
        return result;
    }
    
    
    public static Date parseDate (String value) {
        Date result = null;
        try {
            result = DATE_FORMAT.parse(value);
        } catch (ParseException ex) {
            // do nothing; we will just return null
        }
        return result;
    }
    
    public static Double parseDouble (String value) {
        Double result = null;
        try {
            result = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            // do nothing; we will just return null
        }
        return result;
    }

    public static String getMatch(Pattern pattern, String line) {
        Matcher m = pattern.matcher(line);
        if (!m.find()) {
            throw new RuntimeException();
        }
        return m.group(1);
        
    }
    
    public static String pad (String s) {
        return pad(s, 40);
    }
    
    public static String pad (String s, int size) {
        StringBuilder buf = new StringBuilder(s);
        for (int i = s.length(); i < size; i++) {
            buf.append(" ");
        }
        return buf.toString();
    }
    
    public static String getTimeline (int prepad) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < prepad; i++) {
            buf.append(" ");
        }
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 11; j++) {
                buf.append("+");
            }
            buf.append((i + 1) % 10);
        }
        return buf.toString();
    }
    
    public static String collapseWhitespace (String s) {
        Matcher m1 = WHITESPACE_COLLAPSE_PATTERN1.matcher(s);
        s = m1.replaceAll(" ");
        Matcher m2 = WHITESPACE_COLLAPSE_PATTERN2.matcher(s);
        return m2.replaceAll(" ");
    }
    
    public static Properties getApplicationProperties (Class<?> clazz) throws IOException {
        Properties appProperties = new Properties();
        InputStream in = null;
        try {
            in = clazz.getResourceAsStream("application.properties");
            appProperties.load(in);
            return appProperties;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    public static String replace (Pattern p, String s, String replacement) {
        Matcher m = p.matcher(s);
        return m.replaceFirst(replacement);
        
    }
    
    public static int [] parseIpv4Address (String address) {
        Matcher m = IPV4_PATTERN.matcher(address);
        if (!m.find()) {
            throw new RuntimeException("Invalid IP address: " + address);
        }
        int [] octets = new int[4];
        octets[0] = Integer.parseInt(m.group(1));
        octets[1] = Integer.parseInt(m.group(2));
        octets[2] = Integer.parseInt(m.group(3));
        octets[3] = Integer.parseInt(m.group(4));
        return octets;
    }
    
    public static boolean isValidAddress (String address) {
        try {
            int [] octets = parseIpv4Address(address);
            return isValidAddress(octets);            
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public static boolean isValidAddress (int [] octets) {
        return !(octets[0] == 0 && octets[1] == 0 && octets[2] == 0 && octets[3] == 0);            
    }
    
    public static boolean isValidExternalAddress (String address) {
        try {
            int [] octets = parseIpv4Address(address);
            return isValidExternalAddress(octets);            
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static boolean isValidExternalAddress(int[] octets) {
        return isValidAddress(octets) && !isLoopBackAddress(octets) && !isIanaReservedPrivateNetworkAddress(octets);
    }

    public static boolean isLoopBackAddress (String address) {
        return isLoopBackAddress(parseIpv4Address(address));
    }

    private static boolean isLoopBackAddress(int[] octets) {
        return octets[0] == 127 && octets[1] == 0 && octets[2] == 0 && octets[3] == 1;
    }
    
    public static boolean isIanaReservedPrivateNetworkAddress (String address) {
        return isIanaReservedPrivateNetworkAddress(parseIpv4Address(address));
    }

    private static boolean isIanaReservedPrivateNetworkAddress(int[] octets) {
        return (octets[0] == 10) || (octets[0] == 172 && octets[1] >= 16 && octets[1] < 32) || (octets[0] == 192 && octets[1] == 168);
    }
}
