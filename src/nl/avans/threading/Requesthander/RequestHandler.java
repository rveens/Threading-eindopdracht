package nl.avans.threading.Requesthander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/27/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestHandler
{
    private Socket sok;

    public RequestHandler(Socket sok)
    {
        this.sok = sok;
    }

    public void run() throws IOException {
        HTTPRequestParser reqparser = new HTTPRequestParser(new InputStreamReader(sok.getInputStream()));
        System.out.println(reqparser.parseRequest());

        // Gebruik DataOutPutStream voor schrijven naar een socket.

        // Sluit de socket
    }
}
