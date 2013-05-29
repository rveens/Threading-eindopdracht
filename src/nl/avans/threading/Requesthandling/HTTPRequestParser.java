package nl.avans.threading.Requesthandling;

import nl.avans.threading.WebserverConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/28/13
 * Time: 3:28 PM
 *
 */
public class HTTPRequestParser {

    BufferedReader bufferedReader;
    boolean isValid;
    int[] httpVersion;
    String httpMethod;
    String url;
    Hashtable params;
    Hashtable headers;

    public HTTPRequestParser(InputStreamReader isr)
    {
        bufferedReader = new BufferedReader(isr);
        headers = new Hashtable();
        httpVersion = new int[2];
        httpMethod = "";
        url = "";
    }

    /*
    * Validate the HTTP request
    *
    * @return boolean that represents the validity of the request
    */
    public boolean validateRequest() throws IOException {
        String initialRequestLine;      // Bijvoorbeeld:  GET / HTTP/1.1
        String initialRequestLineWords[];
        String[] temp;
        int i, idx; // idx: index of of the url

        /* We gaan er vanuit dat alles helemaal goed gaat... */
        isValid = true;

        /* Initial Request Line afhandelen */
        initialRequestLine = bufferedReader.readLine();
        System.out.println(initialRequestLine);
        /* Controleer de Initial Request Line op fouten */
        if (initialRequestLine == null || initialRequestLine.length() == 0) {
            // Waarschijnlijk een connectie fout
            isValid = false;
            return isValid;
        }
        if (Character.isWhitespace(initialRequestLine.charAt(0))) {
            // Eerste char mag geen whitespace zijn
            isValid = false;
            return isValid;
        }

        /* split de Initial Request Line op in woorden */
        initialRequestLineWords = initialRequestLine.split("\\s"); // Deel op in woorden
        if (initialRequestLineWords.length != 3) {
            // Als de Initial Request Line niet uit drie woorden bestaat dan is het een bad request.
            isValid = false;
        }
        if (initialRequestLineWords[2].indexOf("HTTP/") == 0 &&
                initialRequestLineWords[2].indexOf('.') > 5) {
            temp = initialRequestLineWords[2].substring(5).split("\\.");

            try {
                httpVersion[0] = Integer.parseInt(temp[0]);
                httpVersion[1] = Integer.parseInt(temp[1]);
            } catch(NumberFormatException nfe) {
                isValid = false;
            }
        } else {
            // Derde woord is niet correct
            isValid = false;
        }

        if (initialRequestLineWords[0].equals(WebserverConstants.GET) ||
                initialRequestLineWords[0].equals(WebserverConstants.POST)) {
            // TODO parameters in de url ondersteunen
            url = initialRequestLineWords[1];
            parseHeaders();
            if (headers == null)
                isValid = false;
        } else {
            // Het eerste woord klopt niet, of de methode wordt niet niet ondersteund.
            isValid = false;
        }

        /* http 1.1 vereist een host header */
        if (httpVersion[0] == 1 && httpVersion[1] >= 1 && getHeader("Host") == null)
            isValid = false;

        return isValid;
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

    public String getHeader(String key) {
        if (headers != null)
            return (String) headers.get(key.toLowerCase());
        else
            return null;
    }
}
