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

    public RequestHandler(Socket sok)
    {
        this.sok = sok;
        logger = Logger.getInstance();
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
                    throw new HTTPInvalidRequestException(405, "method: '" + reqparser.getHttpMethod() + "' not allowed");
                }
            } catch (HTTPInvalidRequestException e) {
                e.printStackTrace();
                sendResponse(e.getResponseCode(), e.getMessage());
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

    protected void handleGETRequest()
    {
        if (reqparser.getUrl().equals("/")) {
            sendResponse(Settings.webRoot + "/" + Settings.defaultPage);
        } else {
            sendResponse(Settings.webRoot + reqparser.getUrl());
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

    protected void sendResponse(String filePath)
    {
        DataOutputStream out = null;

        try {
            out = new DataOutputStream(sok.getOutputStream());
            // create file to read error page content
            File f = new File(filePath);
            if (!f.exists()) {
                sendResponse(404, "Page not found");
                return;
            }
            // send initial line of header
            out.writeBytes(createInitialResponseLine(200, reqparser.getHttpVersion()));
            // send headers (also get file content-type and length)
            out.writeBytes(createResponsHeaders(Files.probeContentType(f.toPath()), (int) f.length()));

            // use fileInputStream for reading bytes from the file
            FileInputStream fis = new FileInputStream(f);
            int count;
            byte[] fileReadBuffer = new byte[WebserverConstants.FILE_READ_CHUNK_SIZE];
            // send binaryData
            while ((count = fis.read(fileReadBuffer)) > 0)
                out.write(fileReadBuffer, 0, count);

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            sendInternalErrorResponse();
        }
    }

    protected void sendResponse(int responseCode, String cause)
    {
        final String ERRORPAGE_LOCATION = "./ErrorPages/404.html";
        DataOutputStream out = null;
        try {
            logger.LogMessage("ERROR: " + responseCode + " - " + cause);
            if (!(responseCode == 400 || responseCode == 401 || responseCode == 404))
                throw new Exception("responseCode not supported");

            //ERROR PAGE LOCATION
            out = new DataOutputStream(sok.getOutputStream());

            // create file to read error page content
            File f = new File(ERRORPAGE_LOCATION);
            if (!f.exists())
                throw new Exception("Path to error page is not valid");
            // send initial line of header
            out.writeBytes(createInitialResponseLine(200, reqparser.getHttpVersion()));
            // send headers (also get file content-type and length)
            out.writeBytes(createResponsHeaders(Files.probeContentType(f.toPath()), (int) f.length()));

            // use fileInputStream for reading bytes from the file
            FileInputStream fis = new FileInputStream(f);
            int count;
            byte[] fileReadBuffer = new byte[WebserverConstants.FILE_READ_CHUNK_SIZE];
            // send binaryData
            while ((count = fis.read(fileReadBuffer)) > 0)
                out.write(fileReadBuffer, 0, count);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalErrorResponse();
        }
    }

    private void sendInternalErrorResponse()
    {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(sok.getOutputStream());
            out.writeBytes(createInitialResponseLine(500, reqparser.getHttpVersion()));
            out.writeBytes(createResponsHeaders("text/html", 0));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
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
