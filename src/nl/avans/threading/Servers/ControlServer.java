package nl.avans.threading.Servers;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Requesthandling.ControlServerRequestHandler;
import nl.avans.threading.Requesthandling.RequestHandler;
import nl.avans.threading.Settings;
import nl.avans.threading.WebserverConstants;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/27/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlServer extends Server
{
    public ControlServer(int port) throws IOException
    {
        super(port);
    }

    @Override
    public void run() {
        pool = Executors.newFixedThreadPool(WebserverConstants.MAX_CONCURRENT_THREADS);

        /*log start*/
        logger.LogMessage("ControlServer started listening on port: " + Settings.controlPort);

        while (true) {
            try {
                /* Wait for a new socket */
                //TODO Socket must be typeof https socket
                Socket sok = socketListen.accept();
                /* Create a requesthandler instance and give it the socket */
                // TODO request handler for ControlServer

                /* Do the actual handling */
                RequestHandler handler = new ControlServerRequestHandler(sok);
                pool.execute(handler);

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
