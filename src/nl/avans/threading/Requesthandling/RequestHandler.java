package nl.avans.threading.Requesthandling;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Settings;
import nl.avans.threading.SettingsIOHandler;
import nl.avans.threading.WebserverConstants;

import javax.activation.FileTypeMap;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/27/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestHandler implements Runnable {
    protected Socket sok;
    protected HTTPRequestParser reqparser;
    protected Logger logger;

    protected String webRoot;

    public RequestHandler(Socket sok)
    {
        this.sok = sok;
        logger = Logger.getInstance();
        webRoot = Settings.webRoot;
    }

    @Override
    public void run() {
        try {
            long timeStart = System.currentTimeMillis(); //TO MEASURE RESPONSE TIME
            DataOutputStream out = null;

            reqparser = new HTTPRequestParser(new InputStreamReader(sok.getInputStream()));
            try {
                /* 1 - Parsen van request */
                reqparser.validateRequest();

                /* 2 - Bestand ophalen of reageren op POST */
                if (reqparser.getHttpMethod().equals("GET")) {
                    handleGETRequest();
                } else if (reqparser.getHttpMethod().equals("POST")) {
                    handlePOSTRequest();

                } else {
                    handleNOTSUPPORTEDRequest();
                }
            } catch (HTTPInvalidRequestException e) {
                e.printStackTrace();

                /* send bad request response */
                if (out == null)
                    out = new DataOutputStream(sok.getOutputStream());
                out.writeBytes(createInitialResponseLine(400, new int[] { 1, 1}));
                out.writeBytes(createResponsHeaders("text/html", 0));
                out.close();
            }

            //LOGGING
            long elapsedTimeMillis = System.currentTimeMillis()-timeStart;
            logger.LogMessage(reqparser.getHttpMethod() + " " + reqparser.getUrl() + " Request took: " + elapsedTimeMillis + "ms" + " <" + sok.getInetAddress().getHostAddress() + ">");

            // TODO Sluit de socket -> niet met http1.1
            if (!reqparser.getHeader("connection").equals("keep-alive"))
                sok.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void handleGETRequest()
    {
        DataOutputStream out = null;

        try {
            /* check if the file exists */
            File f = new File(webRoot + reqparser.getUrl());
            out = new DataOutputStream(sok.getOutputStream());
            if (f.exists()) {
                System.out.println("TEST: File: '" + f.getAbsolutePath() + "' found");
                // TODO determine content-length and add this to the header
                out.writeBytes(createInitialResponseLine(200, reqparser.getHttpVersion()));
                            /* get content type of file */
                out.writeBytes(createResponsHeaders(Files.probeContentType(f.toPath()), (int) f.length()));
                // TODO write file to stream
                            /* fileinputstream for reading bytes from the file */
                FileInputStream fis = new FileInputStream(f);
                int count;
                byte[] fileReadBuffer = new byte[WebserverConstants.FILE_READ_CHUNK_SIZE];
                while ((count = fis.read(fileReadBuffer)) > 0)
                    out.write(fileReadBuffer, 0, count);
            } else {
                System.out.println("TEST: File: '" + f.getPath() + "' not found");
                out.writeBytes(createInitialResponseLine(404, reqparser.getHttpVersion()));
                out.writeBytes(createResponsHeaders("text/html", 0));
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void handlePOSTRequest()
    {
        handleNOTSUPPORTEDRequest();
    }

    private void handleNOTSUPPORTEDRequest()
    {
        DataOutputStream out = null;

        try {
            out = new DataOutputStream(sok.getOutputStream());
            out.writeBytes(createInitialResponseLine(404, reqparser.getHttpVersion()));
            out.writeBytes(createResponsHeaders("text/html", 0));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private String createInitialResponseLine(int statusCode, int[] httpVersion)
    {
        // TODO use stringbuilder
        String initialResponseLine;

        initialResponseLine = "";

        initialResponseLine += "HTTP/";
        initialResponseLine += httpVersion[0];
        initialResponseLine += ".";
        initialResponseLine += httpVersion[1];
        initialResponseLine += " ";
        initialResponseLine += statusCode;
        initialResponseLine += " ";
        initialResponseLine += WebserverConstants.HttpReplies.get(statusCode);
        initialResponseLine += '\n';

        return initialResponseLine;
    }

    // FIXME deze zijn altijd hetzelfde?
    private String createResponsHeaders(String contentType, int contentLength)
    {
        // TODO use stringbuilder
        String headers;
        Date currentTime;

        headers = "";
        currentTime = new Date();

        headers += "Date: " + WebserverConstants.DATE_FORMAT_RESPONSE.format(currentTime) + '\n';
        headers += "Server: SuperWebserver/0.1 (" + System.getProperty("os.name") + ")" + '\n';
        headers += "Content-Type: " + contentType + '\n';
        headers += "Content-Length: " + contentLength + '\n';

        headers += "\r\n";

        return headers;
    }
}
