package nl.avans.threading.Servers;

import nl.avans.threading.Requesthander.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/27/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Server extends Thread {

    ServerSocket socketListen;

    public Server(int port) throws IOException
    {
        socketListen = new ServerSocket(port);
    }

    @Override
    public void run() {
        Socket sok = null;

        while (true) {
            try {
                /* wait for a request */
                sok = socketListen.accept();
                /* Create a requesthandler (thread) instance and give it the socket */
                RequestHandler handler = new RequestHandler(sok);
                /* Create the thread */
                handler.run();

                /* And keep going */
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
