package nl.avans.threading.Logging;

import nl.avans.threading.WebserverConstants;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/29/13
 * Time: 9:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Logger extends Thread
{
    private String[] fifoQueue; // De queue, dit is een verplichtte string-array. TODO fi-lo veranderen in fifo queue
    private int currentSize;    // Huidige grootte van de queue.
    private File logFile;

    public Logger(String logFilePath)
    {
        fifoQueue = new String[WebserverConstants.LOGGER_QUEUE_MAX_SIZE];
        currentSize = 0;

        // TODO log file openen
        logFile = new File(logFilePath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    /*
    * Method used to add logmessages to the queue
    * */
    public void LogMessage(String message)
    {
        // TODO add timestamp etc to message
        // TODO laten wachten als de queue vol is. Of bezet is.
        /* Dit wordt waarschijnlijk een critical section */
        if (currentSize < WebserverConstants.LOGGER_QUEUE_MAX_SIZE)
            fifoQueue[currentSize++] = message;
    }

    /*
    * Run method van de Java Thread classe die wordt ge-overrided.
    * Tijdens het runnen van de Thread worden er berichten van de queue gelezen en geprint.
    * */
      @Override
    public void run()
    {
        super.run();
        /* Dit wordt waarschijnlijk een critical section */
        if (currentSize > 0)
            ; // TODO append a log entry to the log file
    }
}
