package nl.avans.threading.Requesthandling;

import nl.avans.threading.Settings;
import nl.avans.threading.SettingsIOHandler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 9-6-13
 * Time: 13:11
 */
public class ControlServerRequestHandler extends RequestHandler {

    public ControlServerRequestHandler(Socket sok) {
        super(sok);
    }

    @Override
    protected void handleGETRequest()
    {
        if (reqparser.getUrl().equals("/")) {
            sendResponse(Settings.controlWebRoot + "/" + Settings.defaultControlPage);
        } else {
            /* custom requests! */
            if ( reqparser.getUrl().equals("/log") ) {
                /* send log file back */
                sendResponse(Settings.logLocation);
            } else // Do a normal response instead
                sendResponse(Settings.controlWebRoot + reqparser.getUrl());
        }
    }

    @Override
    protected void handlePOSTRequest()
    {
        DataOutputStream out = null;

        Hashtable<String, String> contentBody = reqparser.getContentBody();
        if (contentBody != null) {
            if (SettingsIOHandler.saveChanges(Integer.parseInt(contentBody.get("inputWebPort")),
                    Integer.parseInt(contentBody.get("inputControlPort")),
                    contentBody.get("inputWebroot"),
                    contentBody.get("inputDefaultPage"),
                    false))
                logger.LogMessage("settings file updated by control-panel");
            //TODO SERVER SHOULD REBOOT
        } else {
            //THROW UP AN ERROR PAGE
        }
        //TODO ADD RESPONSE
    }
}
