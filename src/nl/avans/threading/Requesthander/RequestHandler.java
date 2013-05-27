package nl.avans.threading.Requesthander;

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

    public void run()
    {
        // TODO gebruik HTTPRequestParser
        // Gebruik BufferedReader voor lezen van een socket

        // Gebruik DataOutPutStream voor schrijven naar een socket.

        // Sluit de socket
    }
}
