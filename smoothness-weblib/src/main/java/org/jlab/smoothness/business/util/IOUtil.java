package org.jlab.smoothness.business.util;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IO Utilities.
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

    /**
     * Escape XML entities.
     *
     * @param input The String to escape
     * @return An escaped String
     */
    public static String escapeXml(String input) {
        return org.apache.taglibs.standard.functions.Functions.escapeXml(input);
    }

    /**
     * Reads in an InputStream fully and returns the result as a String.
     *
     * @param is       The InputStream
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

    /**
     * Fetch HTML at the given URL with the provided timeouts.
     *
     * @param urlStr The URL
     * @param connectTimeout The connect timeout in milliseconds
     * @param readTimeout The read timeout in milliseconds
     * @return The HTML String
     * @throws IOException If unable to fetch the HTML
     */
    public static String doHtmlGet(String urlStr, int connectTimeout, int readTimeout) throws
            IOException {
        URL url;
        HttpURLConnection con;

        url = new URL(urlStr);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(readTimeout);

        return streamToString(con.getInputStream(), "UTF-8");
    }

    /**
     * Return a random integer between min and max.
     *
     * @param min The min
     * @param max The max
     * @return The random int
     */
    public static int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Convert an array of objects to a Comma Separated Values (CSV) String.
     *
     * @param items The objects
     * @return A CSV String
     */
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

    /**
     * Convert an array of objects to a Space Separated Values (SSV) String.
     *
     * @param items The objects
     * @return The SSV String
     */
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

    /**
     * Return null if input is null, else value of toString().
     *
     * @param o The object
     * @return null or value of toString()
     */
    public static String nullOrString(Object o) {
        String value = null;

        if (o != null) {
            value = o.toString();
        }

        return value;
    }

    /**
     * Return null if input is null, else String Y or N corresponding to true or false respectively.
     *
     * @param b The Boolean
     * @return null or String with "Y" or "N"
     */
    public static String nullOrBoolean(Boolean b) {
        String value = null;

        if (b != null) {
            value = (b == true ? "Y" : "N");
        }

        return value;
    }

    /**
     * Return null if input is null, else String containing formatted date.
     *
     * @param d The date
     * @param dateFormat The format
     * @return null or a formatted String representation of the date
     */
    public static String nullOrFormat(Date d, SimpleDateFormat dateFormat) {
        String value = null;

        if (d != null) {
            value = dateFormat.format(d);
        }

        return value;
    }

    /**
     * Removes any null values from an array.
     *
     * @param a The array
     * @param c The Class of items in the array
     * @param <E> The type of items in the array
     * @return A new array with only the not-null values provided in the input.
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] removeNullValues(final E[] a, Class<E> c) {
        if (a == null) {
            return null;
        }
        List<E> list = new ArrayList<>(Arrays.asList(a));
        list.removeAll(Collections.singleton(null));
        return list.toArray((E[]) Array.newInstance(c, list.size()));
    }

    /**
     * Copies all of the bytes from the InputStream into the OutputStream using
     * a buffer of 4096 bytes.
     *
     * @param in  The InputStream
     * @param out The OutputStream
     * @return The number of bytes copied
     * @throws IOException If unable to copy
     */
    public static long copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0;
        int n = 0;

        while (-1 != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
            count += n;
        }

        return count;
    }

    /**
     * Closes one or more AutoCloseable without generating any checked Exceptions.
     * If an Exception does occur while closing it is logged as a WARNING.
     *
     * @param resources The AutoClosable resources
     */
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

    /**
     * Return null if the input is null, else a Comma Separated Values (CSV) String.
     *
     * @param array The array of BigInteger values
     * @return null or a CSV String
     */
    public static String toNullOrCsv(BigInteger[] array) {
        String csv = null;

        array = IOUtil.removeNullValues(array, BigInteger.class);

        if (array != null && array.length > 0) {
            csv = array[0].toString();

            for (int i = 1; i < array.length; i++) {
                csv = csv + "," + array[i];
            }
        }

        return csv;
    }

    /**
     * Return NULL SQL text if the input is null, else a Comma Separated Values (CSV) String with values quoted for SQL.
     *
     * @param array The array of BigInteger values
     * @return A CSV String
     */
    public static String toNullOrCsvForStoredProcedure(BigInteger[] array) {
        String value = toNullOrCsv(array);
        if (value == null) {
            value = "NULL";
        } else {
            value = "'" + value + "'";
        }
        return value;
    }

    /**
     * Return a SSLSocketFactory that ignores certificates.
     *
     * @return The SSLSocketFactory
     * @throws NoSuchAlgorithmException If unable to find a valid algorithm
     * @throws KeyManagementException If a key issue occurs
     */
    public static SSLSocketFactory getTrustySSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = null;
        context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }}, new SecureRandom());

        return context.getSocketFactory();
    }

    /**
     * Return a Hostname verifier that doesn't actually check anything!
     *
     * @return The HostnameVerifier
     */
    public static HostnameVerifier getTrustyHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }
}
