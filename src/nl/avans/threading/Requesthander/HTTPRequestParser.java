package nl.avans.threading.Requesthander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/23/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */

public class HTTPRequestParser {

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
    private BufferedReader reader;
    private String method, url;
    private Hashtable headers, params;
    private int[] ver;

    /* Constructor */
    public HTTPRequestParser(InputStream is)
    {
        reader = new BufferedReader(new InputStreamReader(is));
    }

    /* parseRequest: parse an */
    public int parseRequest() throws IOException {
        String firstLine;   // first line
        String prms[], cmd[], temp[];
        int ret;            // return value
        int idx;
        int i;

        ret = 200; // default is OK

        firstLine = reader.readLine();
        // if there is no first line return null
        if (firstLine == null || firstLine.length() == 0)
            return 0;
        if (Character.isWhitespace(firstLine.charAt(0))) {
            // start with a whitespace is a bad request
            return 400;
        }

        cmd = firstLine.split("\\s");
        if (cmd.length != 3)
            return 400;

        if (cmd[2].indexOf("HTTP/") == 0 && cmd[2].indexOf('.') > 5) {
            temp = cmd[2].substring(5).split("\\.");
            try {
                ver[0] = Integer.parseInt(temp[0]);
                ver[1] = Integer.parseInt(temp[1]);
            } catch (NumberFormatException nfe) {
                ret = 400;
            }
        } else
            ret = 400;

        if (cmd[0].equals("GET") || cmd[0].equals("HEAD")) {
            method = cmd[0];

            idx = cmd[1].indexOf('?');
            if (idx < 0)
                url = cmd[1];
            else {
                url = URLDecoder.decode(cmd[1].substring(0, idx), "ISO-8859-1");
                prms = cmd[1].substring(idx+1).split("&");

                params = new Hashtable();
                for (i = 0; i < prms.length; i++) {
                    temp = prms[i].split("=");
                    if (temp.length == 2) {
                        // we use ISO-8859-1 as temporary charset and then
                        // String.getBytes("ISO-8859-1") to get the data
                        params.put(URLDecoder.decode(temp[0], "ISO-8859-1"),
                                   URLDecoder.decode(temp[1], "ISO-8859-1"));
                    } else if (temp.length == 1 && prms[i].indexOf('=') == prms[i].length()-1) {
                        // handle empty string separatedly
                        params.put(URLDecoder.decode(temp[0], "ISO-8859-1"), "");
                    }
                }
            }
            parseHeaders();
            if (headers == null)
                ret = 400;
        } else if (cmd[0].equals("POST")) {
            ret = 501; // not implemented
        } else if (ver[0] == 1 && ver[1] >= 1) {
            if (cmd[0].equals("OPTIONS") ||
                cmd[0].equals("PUT") ||
                cmd[0].equals("DELETE") ||
                cmd[0].equals("TRACE") ||
                cmd[0].equals("CONNECT")) {
                ret = 501; // not implemented
            }
        } else {
            // meh not understand, bad request
            ret = 400;
        }

        if (ver[0] == 1 && ver[1] >= 1 && getHeader("Host") == null) {
            ret = 400;
        }

        return ret;
    }

    private void parseHeaders() throws IOException {
        String line;
        int idx;

        // rfc822 allows multiple lines, we don't care now
        line = reader.readLine();
        while (!line.equals("")) {
            idx = line.indexOf(':');
            if (idx < 0) {
                headers = null;
                break;
            } else {
                headers.put(line.substring(0, idx).toLowerCase(), line.substring(idx + 1).trim());
            }
            line = reader.readLine();
        }
    }

    public String getHeader(String key) {
        if (headers != null)
            return (String) headers.get(key.toLowerCase());
        else
            return null;
    }

}
