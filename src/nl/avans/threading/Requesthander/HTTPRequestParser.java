package nl.avans.threading.Requesthander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/23/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPRequestParser {

    private BufferedReader reader;
    private static final String[][] HttpReplies =   {{"100", "Continue"},
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

    /* Constructor */
    public HTTPRequestParser(InputStream is)
    {
        reader = new BufferedReader(new InputStreamReader(is));
    }

    /* parseRequest: parse an */
    public int parseRequest() throws IOException {
        String firstLine;   // first line
        int ret;            // return value

        ret = 200; // default is OK

        firstLine = reader.readLine();
        // if there is no first line return null
        if (firstLine == null || firstLine.length() == 0)
            return 0;
        if (Character.isWhitespace(firstLine.charAt(0))) {
            // start with a whitespace is a bad request
            return 400;
        }

        return ret;
    }
}
