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
            Logger logger = new Logger(Settings.logLocation); // TODO logfile locatie ophalen uit settings-bestand

            /* create server instances */
            ControlServer c = new ControlServer(logger, Settings.controlPort);
            Server s = new Server(logger, Settings.webPort);

            // TODO test weghalen
            logger.LogMessage("Dit is een test");

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
