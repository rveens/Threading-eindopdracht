package nl.avans.threading.Requesthandling;

import nl.avans.threading.WebserverConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/28/13
 * Time: 3:28 PM
 *
 */
public class HTTPRequestParser {

    private BufferedReader bufferedReader;
    private int[] httpVersion;
    private String httpMethod;
    private String url;
    private Hashtable params;
    private Hashtable headers;

    public HTTPRequestParser(InputStreamReader isr)
    {
        bufferedReader = new BufferedReader(isr);
        headers = new Hashtable();
        params = new Hashtable();
        httpVersion = new int[2];
        httpMethod = "";
        url = "";
    }

    /*
    * Validate the HTTP request
    *
    * @return boolean that represents the validity of the request
    */
    public void validateRequest() throws IOException, HTTPInvalidRequestException {
        String initialRequestLine;      // Bijvoorbeeld:  GET / HTTP/1.1
        String initialRequestLineWords[];
        String[] temp;

        /* Initial Request Line afhandelen */
        initialRequestLine = bufferedReader.readLine();
        System.out.println(initialRequestLine);
        /* Controleer de Initial Request Line op fouten */
        if (initialRequestLine == null || initialRequestLine.length() == 0) {
            // Waarschijnlijk een connectie fout
            throw new HTTPInvalidRequestException();
        }
        if (Character.isWhitespace(initialRequestLine.charAt(0))) {
            // Eerste char mag geen whitespace zijn
            throw new HTTPInvalidRequestException();
        }

        /* split de Initial Request Line op in woorden */
        initialRequestLineWords = initialRequestLine.split("\\s"); // Deel op in woorden
        if (initialRequestLineWords.length != 3) {
            // Als de Initial Request Line niet uit drie woorden bestaat dan is het een bad request.
            throw new HTTPInvalidRequestException();
        }
        if (initialRequestLineWords[2].indexOf("HTTP/") == 0 &&
                initialRequestLineWords[2].indexOf('.') > 5) {
            temp = initialRequestLineWords[2].substring(5).split("\\.");

            try {
                httpVersion[0] = Integer.parseInt(temp[0]);
                httpVersion[1] = Integer.parseInt(temp[1]);
            } catch(NumberFormatException nfe) {
                throw new HTTPInvalidRequestException();
            }
        } else {
            // Derde woord is niet correct
            throw new HTTPInvalidRequestException();
        }

        if (initialRequestLineWords[0].equals(WebserverConstants.GET) ||
                initialRequestLineWords[0].equals(WebserverConstants.POST)) {
            // TODO parameters in de url ondersteunen
            httpMethod = initialRequestLineWords[0];

            parseHeaders();
            if (headers == null)
                throw new HTTPInvalidRequestException();
            /* TEST: print body */
            String line;
            while ( (line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } else {
            // Het eerste woord klopt niet, of de methode wordt niet niet ondersteund.
            throw new HTTPInvalidRequestException();
        }

        /* http 1.1 vereist een host header */
        if (httpVersion[0] == 1 && httpVersion[1] >= 1 && getHeader("Host") == null)
            throw new HTTPInvalidRequestException();
    }

    /* Read through the lines of the header and put them in a hashtable */
    private void parseHeaders() throws IOException {
        String line;    // current line
        int index;      // position index of the current line

        line = bufferedReader.readLine();
        while (!line.equals("")) {
            System.out.println(line);
            index = line.indexOf(':');
            /* Als de regel geen ':' karakter bevat, dan is er iets goed mis. */
            if (index < 0) {
                headers = null;
                break;
            } else
                headers.put(line.substring(0, index).toLowerCase(), line.substring(index + 1).trim());
            line = bufferedReader.readLine();
        }
    }

    private void parseParameters(String url)
    {
        int paramStartIndex;    // index van '?'
        String tempParams[];    // temp array om alle parameters in op te slaan
        String temp[];          // temp arrat om tijdens de lus een result op te slaan
        int i;                  // index voor loopen door de for-lus

        paramStartIndex = url.indexOf('?');

        if (paramStartIndex < 0)
            this.url = url;
        else {
            url = URLDecoder.decode(url.substring(0, paramStartIndex));
            tempParams = url.substring(paramStartIndex + 1).split("&");

            for (i = 0; i < tempParams.length; i++) {
                temp = tempParams[i].split("=");
                if (temp.length == 2) {
                    params.put(URLDecoder.decode(temp[0]), URLDecoder.decode(temp[1]));
                } else if (temp.length == 1 && tempParams[i].indexOf('=') == tempParams[i].length()-1) {
                    params.put(URLDecoder.decode(temp[0]), "");
                }
            }
        }
        System.out.println(params);
    }

    public String getHeader(String key) {
        if (headers != null)
            return (String) headers.get(key.toLowerCase());
        else
            return null;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public int[] getHttpVersion() {
        return httpVersion;
    }

    public String getUrl() {
        return url;
    }
}
