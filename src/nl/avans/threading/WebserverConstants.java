package nl.avans.threading;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/28/13
 * Time: 3:35 PM
 */
public class WebserverConstants {

    public static final Hashtable<Integer, String> HttpReplies;
    // Fill HttpReplies with values
    static {
        HttpReplies = new Hashtable<Integer, String>();
        String[][] replyPairs = {{"100", "Continue"},
                                {"101", "Switching Protocols"},
                                {"200", "OK"},
                                {"201", "Created"},
                                {"202", "Accepted"},
                                {"203", "Non-Authoritative Information"},
                                {"204", "No Content"},
                                {"205", "Reset Content"},
                                {"206", "Partial Content"},
                                {"300", "Multiple Choices"},
                                {"301", "Moved Permanently"},
                                {"302", "Found"},
                                {"303", "See Other"},
                                {"304", "Not Modified"},
                                {"305", "Use Proxy"},
                                {"306", "(Unused)"},
                                {"307", "Temporary Redirect"},
                                {"400", "Bad Request"},
                                {"401", "Unauthorized"},
                                {"402", "Payment Required"},
                                {"403", "Forbidden"},
                                {"404", "Not Found"},
                                {"405", "Method Not Allowed"},
                                {"406", "Not Acceptable"},
                                {"407", "Proxy Authentication Required"},
                                {"408", "Request Timeout"},
                                {"409", "Conflict"},
                                {"410", "Gone"},
                                {"411", "Length Required"},
                                {"412", "Precondition Failed"},
                                {"413", "Request Entity Too Large"},
                                {"414", "Request-URI Too Long"},
                                {"415", "Unsupported Media Type"},
                                {"416", "Requested Range Not Satisfiable"},
                                {"417", "Expectation Failed"},
                                {"500", "Internal Server Error"},
                                {"501", "Not Implemented"},
                                {"502", "Bad Gateway"},
                                {"503", "Service Unavailable"},
                                {"504", "Gateway Timeout"},
                                {"505", "HTTP Version Not Supported"}};
        for (String[] pair : replyPairs)
            HttpReplies.put(Integer.parseInt(pair[0]), pair[1]);
    }

    public static final Hashtable<Integer, String> HTTP_ERRORPAGE_LOCATIONS;
    static {
        HTTP_ERRORPAGE_LOCATIONS = new Hashtable<Integer, String>();
        String[][] pairs = {{"400", "./ErrorPages/400.html"},
                            {"401", "./ErrorPages/401.html"},
                            {"404", "./ErrorPages/404.html"}};
        for (String[] pair : pairs)
            HTTP_ERRORPAGE_LOCATIONS.put(Integer.parseInt(pair[0]), pair[1]);
    }

    public final static String
            GET = "GET",
            POST = "POST",
            HEAD = "HEAD",
            PUT = "PUT",
            OPTIONS = "OPTIONS",
            DELETE = "DELETE";

    public final static int MAX_CONCURRENT_THREADS = 10;
    public final static int LOGGER_QUEUE_MAX_SIZE = 10;
    public final static DateFormat DATE_FORMAT_RESPONSE = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH); // Mon, 23 May 2005 22:38:34 GMT
    public final static DateFormat DATE_FORMAT_LOG      = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //2013-06-03 19:36:22
    static {
        DATE_FORMAT_RESPONSE.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public final static int FILE_READ_CHUNK_SIZE = 1024;

    public static final int SECURITYLEVEL_ADMIN = 0;
    public static final int SECURITYLEVEL_SUPERUSER = 1;
    public static final int SECURITYLEVEL_USER = 2;
    public static final int MAX_SESSION_AGE = 300000; //300.000ms == 5 minutes
}