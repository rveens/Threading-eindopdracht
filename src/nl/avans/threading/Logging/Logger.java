package nl.avans.threading.Logging;

import nl.avans.threading.WebserverConstants;

import java.io.*;
import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/29/13
 * Time: 9:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Logger extends Thread
{
    private File logFile;                   // Bestand waar de regels naar weggeschreven worden

    private String[] fifoQueue;             // De queue, dit is een string-array. Dit is verplicht namens de opdracht.
    private int startIndex;                 // Start van de circular queue
    private int endIndex;                   // Einde van de circular queue
    private int currentSize;

    public Logger(String logFilePath)
    {
        fifoQueue = new String[WebserverConstants.LOGGER_QUEUE_MAX_SIZE];
        currentSize = startIndex = endIndex = 0;

        try {
            /* open de file */
            logFile = new File(logFilePath);
                /* doe wat checks */
            if (logFile.isFile())
                logFile.createNewFile();
            else if (logFile.isDirectory())
                throw new FileNotFoundException("error: file is a directory");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Logmessages toevoegen aan de queue
    */
    public void LogMessage(final String newMessage)
    {
        new Thread() {
            public void run()
            {
                // TODO add timestamp etc to message
                synchronized (fifoQueue) {
                    while (currentSize == WebserverConstants.LOGGER_QUEUE_MAX_SIZE - 1) {
                        try {
                            fifoQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }

                    /* zet de nieuwe message vooraan in de queue */
                    fifoQueue[endIndex] = newMessage;
                    /* vergroot de eindmarkeering met 1 */
                    endIndex = (endIndex+1) % fifoQueue.length;
                    currentSize++;

                    /* Maak de logger wakker */
                    fifoQueue.notify();
                }
            }
        }.start();

    }

    /*
    * Run method van de Java Thread classe die wordt ge-overrided.
    * Tijdens het runnen van de Thread worden er berichten van de queue gelezen en weggeschreven naar de file.
    * */
    @Override
    public void run()
    {
        super.run();

        synchronized (fifoQueue) {
            while (true) {

                /* Mocht de queue leeg zijn, dan moet er worden gewacht */
                while (currentSize == 0) {
                    try {
                        fifoQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                try {
                    /* maak een writer object aan. Als de file niet bestaat wordt deze bij deze aangemaakt */
                    BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));

                    /* schrijf de regel vooraan in de queue weg */
                    if (fifoQueue[startIndex] != null) {
                        out.write(fifoQueue[startIndex]);
                        out.newLine();
                    }
                    /* verklein de beginmarkeering met 1 */
                    startIndex = (startIndex+1) % fifoQueue.length;

                    /* sluit de writer */
                    out.close(); // NIET VERGETEN
                    currentSize--;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /* maak een thread wakker die een bericht wil loggen */
                fifoQueue.notify();
            }
        }
    }
}
