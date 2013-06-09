package nl.avans.threading.Requesthandling;

import nl.avans.threading.Logging.Logger;
import nl.avans.threading.Settings;
import nl.avans.threading.SettingsIOHandler;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 9-6-13
 * Time: 13:11
 */
public class ControlServerRequestHandler extends RequestHandler {

    public ControlServerRequestHandler(Socket sok) {
        super(sok);
        webRoot = Settings.controlWebRoot;
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
