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
    private Hashtable<String, String> contentBody;

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
            throw new HTTPInvalidRequestException(400, "Initial request-line may not be null");
        }
        if (Character.isWhitespace(initialRequestLine.charAt(0))) {
            // Eerste char mag geen whitespace zijn
            throw new HTTPInvalidRequestException(400, "Initial request-line first character cannot be ' '");
        }

        /* split de Initial Request Line op in woorden */
        initialRequestLineWords = initialRequestLine.split("\\s"); // Deel op in woorden
        if (initialRequestLineWords.length != 3) {
            // Als de Initial Request Line niet uit drie woorden bestaat dan is het een bad request.
            throw new HTTPInvalidRequestException(400, "Initial request-line not well-formed");
        }
        if (initialRequestLineWords[2].indexOf("HTTP/") == 0 &&
                initialRequestLineWords[2].indexOf('.') > 5) {
            temp = initialRequestLineWords[2].substring(5).split("\\.");

            try {
                httpVersion[0] = Integer.parseInt(temp[0]);
                httpVersion[1] = Integer.parseInt(temp[1]);
            } catch(NumberFormatException nfe) {
                throw new HTTPInvalidRequestException(400, "undefined");
            }
        } else {
            // Derde woord is niet correct
            throw new HTTPInvalidRequestException(400, "undefined");
        }

        if (initialRequestLineWords[0].equals(WebserverConstants.GET) ||
                initialRequestLineWords[0].equals(WebserverConstants.POST)) {
            // TODO parameters in de url ondersteunen
            httpMethod = initialRequestLineWords[0];

            parseHeaders();
            if (headers == null)
                throw new HTTPInvalidRequestException(400, "Headers could not be processed");

            this.url = initialRequestLineWords[1];

            /* TEST: print body */
            if (headers.get("content-length") != null) {
                char[] buffer = new char[Integer.parseInt((String)headers.get("content-length"))];
                bufferedReader.read(buffer, 0, buffer.length);
                try {
                    parseContentbody(new String(buffer));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(buffer);
            }
        } else {
            // Het eerste woord klopt niet, of de methode wordt niet niet ondersteund.
            throw new HTTPInvalidRequestException(400, "Only GET and POST requests are supported");
        }

        /* http 1.1 vereist een host header */
        if (httpVersion[0] == 1 && httpVersion[1] >= 1 && getHeader("Host") == null)
            throw new HTTPInvalidRequestException(400, "Header 'Host' must be provided");
        //bufferedReader.close();
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

    private void parseContentbody(String bodyContentAsText) throws Exception
    {
        if (bodyContentAsText != null)
        {
            String[] params = bodyContentAsText.split("&");
            contentBody = new Hashtable<String, String>(params.length);
            for (String param : params)
            {
                String[] paramPair = param.split("=");
                if (paramPair.length != 2)
                    throw new Exception("Contentbody is not well formed");
                contentBody.put(paramPair[0], paramPair[1]);
            }
        }
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

    public Hashtable<String, String> getContentBody() {
        return contentBody;
    }
}
