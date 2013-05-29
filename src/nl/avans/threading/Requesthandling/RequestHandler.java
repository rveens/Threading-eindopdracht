package nl.avans.threading.Requesthandling;

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
public class RequestHandler implements Runnable {
    private Socket sok;
    private HTTPRequestParser reqparser;

    public RequestHandler(Socket sok)
    {
        this.sok = sok;
    }

    @Override
    public void run() {
        try {
            reqparser = new HTTPRequestParser(new InputStreamReader(sok.getInputStream()));
            if(reqparser.validateRequest())
                System.out.println("Valid request");
            else
                System.out.println("Invalid request");

            // Gebruik DataOutPutStream voor schrijven naar een socket.
            // Sluit de socket
            //sok.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
