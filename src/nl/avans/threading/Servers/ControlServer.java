package nl.avans.threading.Servers;

import com.sun.net.ssl.internal.ssl.*;
import com.sun.net.ssl.internal.ssl.Provider;
import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Requesthandling.ControlServerRequestHandler;
import nl.avans.threading.Requesthandling.RequestHandler;
import nl.avans.threading.Settings;
import nl.avans.threading.WebserverConstants;

import javax.net.ssl.*;
import javax.security.cert.CertificateException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    protected ServerSocket createServerSocket(int port) throws IOException
    {
        ServerSocket serverSocket = null;

        /* registreer de jsse provider */
        Security.addProvider(new Provider());

        /* specificeer keystore and password */
        System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "appelflap");
        /* debug info */
//        System.setProperty("javax.net.debug", "all");

        /* maak de secure server socket */
        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        serverSocket =  sslserversocketfactory.createServerSocket(port);

        return serverSocket;
    }

    @Override
    public void run() {
        pool = Executors.newFixedThreadPool(WebserverConstants.MAX_CONCURRENT_THREADS); // TODO maar 1 thread pool voor allebei de servers?

        /*log start*/
        logger.LogMessage("ControlServer started listening on port: " + Settings.controlPort);

        while (isRunning) {
            /* Wait for a new socket */
            //TODO Socket must be typeof https socket
            Socket sok = null; // TODO gebruik ssl socket en zet deze in global scope
            try {
                sok = socketListen.accept();
                /* Create a requesthandler instance and give it the socket */
                // TODO request handler for ControlServer

                /* Do the actual handling */
                RequestHandler handler = new ControlServerRequestHandler(sok);
                Future<?> future = pool.submit(handler); // submit ipv execute, want dan krijgen we een Future terug!
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ExecutionException e) {
                    doRestart();
                    throw new RestartWebServerRunTimeException("restart server");
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        pool.shutdownNow();
    }
}
