package nl.avans.threading.Requesthandling;

import nl.avans.threading.WebserverConstants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

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

    public RequestHandler(Socket sok)
    {
        this.sok = sok;
    }

    @Override
    public void run() {
        try {
            reqparser = new HTTPRequestParser(new InputStreamReader(sok.getInputStream()));
            try {
                /* 1 - parsen van request */
                reqparser.validateRequest();

                /* 2 - bestand ophalen of reageren op POST */
                // TODO functie maken voor afhandelen get, post

                /* 3 - response terugsturen */
                sendResponse(200, reqparser.getHttpVersion());
            } catch (HTTPInvalidRequestException e) {
                e.printStackTrace();
                sendResponse(400, reqparser.getHttpVersion());
            }

            // Sluit de socket -> niet met http1.1
            sok.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void sendResponse(int statusCode, int[] httpVersion)
    {
        String initialResponseLine, headers, body;
        Date currentTime;

        initialResponseLine = headers = body = "";
        currentTime = new Date();

        initialResponseLine += "HTTP/";
        initialResponseLine += httpVersion[0];
        initialResponseLine += ".";
        initialResponseLine += httpVersion[1];
        initialResponseLine += " ";
        initialResponseLine += statusCode;
        initialResponseLine += " ";
        initialResponseLine += WebserverConstants.HttpReplies.get(statusCode);
        initialResponseLine += '\n';

        headers += "Date: " + WebserverConstants.DATE_FORMAT.format(currentTime) + '\n';
        headers += "Server: SuperWebserver/0.1 (" + System.getProperty("os.name") + ")" + '\n';
        headers += "Content-Type: text/html" + '\n';

        headers += "\r\n";

        try {
            DataOutputStream out = new DataOutputStream(sok.getOutputStream());
            out.writeBytes(initialResponseLine);
            out.writeBytes(headers);
            // TODO opgevraagde bestand lezen bij een GET
            out.writeBytes("appelflap");

            out.close(); // NIET VERGETEN
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
