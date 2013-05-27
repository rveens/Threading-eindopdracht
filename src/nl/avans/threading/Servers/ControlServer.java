package nl.avans.threading.Servers;

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
    public void Run() throws IOException
    {
        while (true) {
            /* Wait for a new socket */
            Socket sok = socketListen.accept();
            /* Create a requesthandler instance and give it the socket */
            // TODO request handler for ControlServer

            /* Do the actual handling */
        }
    }
}
