package nl.avans.threading.Requesthandling;

import nl.avans.threading.DataIOHandler;
import nl.avans.threading.Settings;
import nl.avans.threading.SettingsIOHandler;

import java.io.DataOutputStream;
import java.io.IOException;
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
    }

    @Override
    protected void handleGETRequest()
    {
        if (reqparser.getUrl().equals("/")) {
            sendResponse(Settings.controlWebRoot + "/" + Settings.defaultControlPage);
        } else {
            sendResponse(Settings.controlWebRoot + reqparser.getUrl());
        }
    }

    @Override
    protected void handlePOSTRequest() throws HTTPInvalidRequestException
    {
        Hashtable<String, String> contentBody = reqparser.getContentBody();
        if (contentBody != null) {
            if (contentBody.get("username") != null) { // check if post came from login-page
                if (handleLoginFormData(contentBody)) {
                    logger.LogMessage("Login attempt succeeded");
                    sendResponse(Settings.controlWebRoot + "/settings.html");
                }
                else {
                    logger.LogMessage("Login attempt failed");
                    sendResponse(Settings.controlWebRoot + "/login.html");
                }
            } else if (contentBody.get("inputWebPort") != null) { // check if post came from settings-page
                if (handleSettingsFormData(contentBody)) {
                    logger.LogMessage("Settings change attempt succeeded");
                    sendTextResponse("Settings applied, server will restart"); //GIVE PAGE WITH SERVER WILL REBOOT
                    //TODO SERVER SHOULD REBOOT
                }
                else {
                    logger.LogMessage("Settings change attempt failed");
                    sendResponse(Settings.controlWebRoot + "/settings.html");
                }
            } else {
                throw new HTTPInvalidRequestException(400, "POST for this form not required");
            }
        } else {
            //THROW UP AN ERROR PAGE
        }
    }

    private boolean handleLoginFormData(Hashtable<String, String> contentBody)
    {
        DataIOHandler dataIOHandler = DataIOHandler.getInstance();
        return (dataIOHandler.verifyUserPassword(contentBody.get("username"), contentBody.get("password")));
    }

    private boolean handleSettingsFormData(Hashtable<String, String> contentBody)
    {
        return (SettingsIOHandler.saveChanges(Integer.parseInt(contentBody.get("inputWebPort")),
                Integer.parseInt(contentBody.get("inputControlPort")),
                contentBody.get("inputWebroot"),
                contentBody.get("inputDefaultPage"),
                false));
    }
}
