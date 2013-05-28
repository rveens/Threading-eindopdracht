package nl.avans.threading;

import nl.avans.threading.Servers.ControlServer;
import nl.avans.threading.Servers.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        try {
            /* create server instances */
            ControlServer c = new ControlServer(8081);
            Server s = new Server(8080);

            /* start servers */
            c.start();
            s.start();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
