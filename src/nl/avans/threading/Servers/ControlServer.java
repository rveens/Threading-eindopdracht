package nl.avans.threading.Servers;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Settings;

import java.io.IOException;
import java.net.Socket;

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
        /*log start*/
        logger.LogMessage("ControlServer started listening on port: " + Settings.controlPort);

        while (true) {
            try {
                /* Wait for a new socket */
                Socket sok = socketListen.accept();
                /* Create a requesthandler instance and give it the socket */
                // TODO request handler for ControlServer

                /* Do the actual handling */

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
