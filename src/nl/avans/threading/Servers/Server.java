package nl.avans.threading.Servers;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Requesthandling.RequestHandler;
import nl.avans.threading.Settings;
import nl.avans.threading.WebserverConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/27/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Server extends Thread {

    protected ServerSocket socketListen;  // Serversocket that wait (err, blocking call) for requests
    protected ExecutorService pool;       // Thread pool for limiting thread creation
    protected Logger logger;
    protected static boolean isRunning = true;

    public Server(int port) throws IOException
    {
        socketListen = createServerSocket(port);
        this.logger = Logger.getInstance();
    }

    protected ServerSocket createServerSocket(int port) throws IOException
    {
        return new ServerSocket(port);
    }

    @Override
    public void run() {
        pool = Executors.newFixedThreadPool(WebserverConstants.MAX_CONCURRENT_THREADS);

        /*log start*/
        logger.LogMessage("Server started listening on port: " + Settings.webPort);

        while (isRunning) {
            try {
                Socket sok = null;
                /* Wait for a request */
                sok = socketListen.accept();

                /* Execute the request handling on another thread */
                RequestHandler handler = new RequestHandler(sok);
                pool.execute(handler);
            } catch (SocketException se) {
                se.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            /* Keep going */
        }
        pool.shutdown();
    }

    public void doRestart() throws IOException {
        isRunning = false;
        socketListen.close(); // sluit de server socket, want hij kan nog aan het wachten zijn
    }

}
