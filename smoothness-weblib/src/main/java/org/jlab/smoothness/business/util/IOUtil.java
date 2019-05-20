package org.jlab.smoothness.business.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
public final class IOUtil {

    private static final Logger LOGGER = Logger.getLogger(
            IOUtil.class.getName());

    /*Only gets seeded once per JVM invocation*/
    private static final Random random = new Random();   

    private IOUtil() {
        // private constructor
    }

    /* My implementation is terrible so just delegate to fn:escapeXml taglib impl instead */
    public static String escapeXml(String input) {
        /*String output = input;

        // Note: if input contains entities already this will break!
        
        if (input != null) {
            output = output.replace("&", "&#038;"); // Must do this one first as & within other replacements
            output = output.replace("\"", "&#034;");            
            output = output.replace("'", "&#039;");
            output = output.replace("<", "&#060;");
            output = output.replace(">", "&#062;");
        }
        return output;*/
        
        return org.apache.taglibs.standard.functions.Functions.escapeXml(input);
    }

    /**
     * Reads in an InputStream fully and returns the result as a String.
     *
     * @param is The InputStream
     * @param encoding The character encoding of the String
     * @return The String representation of the data
     */
    public static String streamToString(InputStream is, String encoding) {
        String str = "";

        Scanner scan = new Scanner(is, encoding).useDelimiter("\\A");

        if (scan.hasNext()) {
            str = scan.next();
        }

        return str;
    }

    public static String doHtmlGet(String urlStr, int connectTimeout, int readTimeout) throws
            MalformedURLException, ProtocolException, IOException {
        URL url;
        HttpURLConnection con;

        url = new URL(urlStr);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        /*con.setUseCaches(false);
         con.setRequestProperty("Pragma", "no-cache");
         con.setRequestProperty("Cache-Control", "no-cache");*/
        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(readTimeout);

        return streamToString(con.getInputStream(), "UTF-8");
    }

    public static int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static String toCsv(Object[] items) {
        String csv = "";

        if (items != null && items.length > 0) {
            if (items[0] == null) {
                throw new IllegalArgumentException("Object must not be null");
            }

            csv = items[0].toString();
            for (int i = 1; i < items.length; i++) {
                if (items[i] == null) {
                    throw new IllegalArgumentException("Object must not be null");
                }
                csv = csv + "," + items[i].toString();
            }
        }

        return csv;
    }
    
    public static String toSsv(Object[] items) {
        String ssv = "";

        if (items != null && items.length > 0) {
            if (items[0] == null) {
                throw new IllegalArgumentException("Object must not be null");
            }

            ssv = items[0].toString();
            for (int i = 1; i < items.length; i++) {
                if (items[i] == null) {
                    throw new IllegalArgumentException("Object must not be null");
                }
                ssv = ssv + " " + items[i].toString();
            }
        }

        return ssv;
    }    
    
    public static String nullOrString(Object o) {
        String value = null;

        if (o != null) {
            value = o.toString();
        }

        return value;
    }    
    
    public static String nullOrBoolean(Boolean b) {
        String value = null;

        if (b != null) {
            value = (b == true ? "Y" : "N");
        }

        return value;
    }    
    
    public static String nullOrFormat(Date d, SimpleDateFormat dateFormat) {
        String value = null;

        if (d != null) {
            value = dateFormat.format(d);
        }

        return value;
    }    
    
    @SuppressWarnings("unchecked")
    public static <E> E[] removeNullValues(final E[] a, Class<E> c) {
        if (a == null) {
            return null;
        }
        List<E> list = new ArrayList<>(Arrays.asList(a));
        list.removeAll(Collections.singleton(null));
        return list.toArray((E[]) Array.newInstance(c, list.size()));
    }

    public static void close(AutoCloseable... resources) {
        if (resources != null) {
            for (AutoCloseable resource : resources) {
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Unable to close resource", e);
                    }
                }
            }
        }
    }
}
