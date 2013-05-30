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
    private final Semaphore currentSize =   // Een semaphore die de huidige grootte van de queue representeert.
            new Semaphore(WebserverConstants.LOGGER_QUEUE_MAX_SIZE, true);

    public Logger(String logFilePath)
    {
        fifoQueue = new String[WebserverConstants.LOGGER_QUEUE_MAX_SIZE];
        startIndex = endIndex = 0;

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
    * */
    public void LogMessage(String newMessage)
    {
        // TODO add timestamp etc to message
        try {
            currentSize.acquire();

            /* zet de nieuwe message vooraan in de queue */
            fifoQueue[endIndex] = newMessage;
            /* vergroot de eindmarkeering met 1 */
            endIndex = (endIndex+1) % fifoQueue.length;

            currentSize.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            try {
                currentSize.acquire();

                /* maak een writer object aan. Als de file niet bestaat wordt deze bij deze aangemaakt */
                BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));

                /* schrijf de regel vooraan in de queue weg */
                out.write(fifoQueue[startIndex]);
                out.newLine();
                /* verklein de beginmarkeering met 1 */
                startIndex = (startIndex+1) % fifoQueue.length;

                /* sluit de writer */
                out.close(); // NIET VERGETEN

                currentSize.release();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
