package nl.avans.threading;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/28/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebserverConstants {

    public static final String[][] HttpReplies =   {{"100", "Continue"},
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
    public final static String
            GET = "GET",
            POST = "POST",
            HEAD = "HEAD",
            PUT = "PUT",
            OPTIONS = "OPTIONS",
            DELETE = "DELETE";

    public final static String
            MIMETYPE_TEXT_HTML = "text/html",
            MIMETYPE_TEXT_PLAIN = "text/plain",
            MIMETYPE_TEXT_XML = "text/xml",
            MIMETYPE_TEXT_HTML_8859_1 = "text/html; charset=iso-8859-1",
            MIMETYPE_TEXT_PLAIN_8859_1 = "text/plain; charset=iso-8859-1",
            MIMETYPE_TEXT_XML_8859_1 = "text/xml; charset=iso-8859-1",
            MIMETYPE_TEXT_HTML_UTF_8 = "text/html; charset=utf-8",
            MIMETYPE_TEXT_PLAIN_UTF_8 = "text/plain; charset=utf-8",
            MIMETYPE_TEXT_XML_UTF_8 = "text/xml; charset=utf-8";

    public final static int MAX_CONCURRENT_THREADS = 10;
}
