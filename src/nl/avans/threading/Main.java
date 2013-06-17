package nl.avans.threading;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Servers.ControlServer;
import nl.avans.threading.Servers.RestartWebServerRunTimeException;
import nl.avans.threading.Servers.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        SettingsIOHandler.loadSettings();
        try {
            Logger logger = Logger.getInstance();

            /* create server instances */
            final ControlServer c = new ControlServer(Settings.controlPort);
            final Server s = new Server(Settings.webPort);

            /* start de logger */
            logger.start();

            /* start servers */
            c.start();
            s.start();


            c.setUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    if (e instanceof RestartWebServerRunTimeException) {
                        try {
                            s.doRestart();
                            System.exit(0);
                        } catch (IOException e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
            });

            /* de main thread gaat na dit niet meteen door */
            s.join();
            c.join();
            logger.doClose(); // sluit de logger!
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
