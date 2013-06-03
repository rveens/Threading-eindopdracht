package nl.avans.threading;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Servers.ControlServer;
import nl.avans.threading.Servers.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        SettingsIOHandler.loadSettings();
        try {
            Logger logger = Logger.getInstance();

            /* create server instances */
            ControlServer c = new ControlServer(Settings.controlPort);
            Server s = new Server(Settings.webPort);

            /* start de logger */
            logger.start();

            /* start servers */
            c.start();
            s.start();

            s.join(); // Wacht op de server thread! Als deze sluit eindigd het programma //TODO wachten op controlserver?
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
