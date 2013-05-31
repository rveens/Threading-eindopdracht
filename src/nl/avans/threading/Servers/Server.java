package nl.avans.threading.Servers;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Requesthandling.RequestHandler;
import nl.avans.threading.WebserverConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/27/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Server extends Thread {

    ServerSocket socketListen;  // Serversocket that wait (err, blocking call) for requests
    ExecutorService pool;       // Thread pool for limiting thread creation

    public Server(Logger logger, int port) throws IOException
    {
        socketListen = new ServerSocket(port);
    }

    @Override
    public void run() {
        pool = Executors.newFixedThreadPool(WebserverConstants.MAX_CONCURRENT_THREADS);

        while (true) {
            try {
                Socket sok = null;
                /* Wait for a request */
                sok = socketListen.accept();
                /* Execute the request handling on another thread */
                RequestHandler handler = new RequestHandler(sok);
                pool.execute(handler);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            /* Keep going */
        }
    }
}
