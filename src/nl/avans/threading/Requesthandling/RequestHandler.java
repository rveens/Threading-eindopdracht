package nl.avans.threading.Requesthandling;

import nl.avans.threading.Settings;
import nl.avans.threading.SettingsIOHandler;
import nl.avans.threading.WebserverConstants;
import nl.avans.threading.Logging.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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
    private Socket sok;
    private HTTPRequestParser reqparser;
    private Logger logger;

    public RequestHandler(Socket sok)
    {
        this.sok = sok;
        this.logger = Logger.getInstance();
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
                    /* check if the file exists */
                    File f = new File(Settings.webRoot + reqparser.getUrl());
                    out = new DataOutputStream(sok.getOutputStream());
                    if (f.exists()) {
                        System.out.println("TEST: File: '" + f.getPath() + "' found");
                        out.writeBytes(createInitialResponseLine(200, reqparser.getHttpVersion()));
                        out.writeBytes(createResponsHeaders());
                        // TODO write file to stream end add a newline
                    } else {
                        System.out.println("TEST: File: '" + f.getPath() + "' not found");
                        out.writeBytes(createInitialResponseLine(404, reqparser.getHttpVersion()));
                        out.writeBytes(createResponsHeaders());
                    }

                    out.close();
                } else if (reqparser.getHttpMethod().equals("POST")) {
                    Hashtable<String, String> contentBody = reqparser.getContentBody();
                    if (contentBody != null) {
                        if (SettingsIOHandler.saveChanges(  Integer.parseInt(contentBody.get("inputWebPort")),
                                                            Integer.parseInt(contentBody.get("inputControlPort")),
                                                            contentBody.get("inputWebroot"),
                                                            contentBody.get("inputDefaultPage"),
                                                            false))
                            logger.LogMessage("settings file updated by control-panel");
                            //TODO SERVER SHOULD REBOOT
                    } else {
                        //THROW UP AN ERROR PAGE
                    }

                } else {
                    out = new DataOutputStream(sok.getOutputStream());
                    out.writeBytes(createInitialResponseLine(404, reqparser.getHttpVersion()));
                    out.writeBytes(createResponsHeaders());
                    out.close();
                }
                    ; // TODO error terugsturen
            } catch (HTTPInvalidRequestException e) {
                e.printStackTrace();

                /* send bad request response */
                if (out == null)
                    out = new DataOutputStream(sok.getOutputStream());
                out.writeBytes(createInitialResponseLine(400, new int[] { 1, 1}));
                out.writeBytes(createResponsHeaders());
                out.close();
            }

            //LOGGING
            long elapsedTimeMillis = System.currentTimeMillis()-timeStart;
            logger.LogMessage(reqparser.getHttpMethod() + " " + reqparser.getUrl() + " Request took: " + elapsedTimeMillis + "ms" + " <" + sok.getInetAddress().getHostAddress() + ">");

            // Sluit de socket -> niet met http1.1
            sok.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private String createInitialResponseLine(int statusCode, int[] httpVersion)
    {
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
    private String createResponsHeaders()
    {
        String headers;
        Date currentTime;

        headers = "";
        currentTime = new Date();

        headers += "Date: " + WebserverConstants.DATE_FORMAT_RESPONSE.format(currentTime) + '\n';
        headers += "Server: SuperWebserver/0.1 (" + System.getProperty("os.name") + ")" + '\n';
        headers += "Content-Type: text/html" + '\n';

        headers += "\r\n";

        return headers;
    }
}
