package nl.avans.threading.Requesthandling;

import nl.avans.threading.DataIOHandler;
import nl.avans.threading.Settings;
import nl.avans.threading.SettingsIOHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        } else if (reqparser.getUrl().equals("/log"))
            sendResponse(Settings.logLocation);
        else if (reqparser.getUrl().equals("/settings.html"))
            handleGETsettingsRequest();
        else if (reqparser.getUrl().equals("/users.html"))
            handleGETusersRequest();
        else {
            sendResponse(Settings.controlWebRoot + reqparser.getUrl());
        }
    }

    private void handleGETusersRequest()
    {
        DataIOHandler handler = DataIOHandler.getInstance();
        ArrayList<String[]> usrdata = handler.getUsersData();

        try {
            /* stop de huidige settings in een kopie van users.html */
            File ft = File.createTempFile("tempusers", ".tmp");
            File settings = new File(Settings.controlWebRoot + "/users.html");
            Files.copy(settings.toPath(), ft.toPath(), StandardCopyOption.REPLACE_EXISTING);

            /* we gebruiken jsoup */
            Document doc = Jsoup.parse(ft, "UTF-8"); // inladen

            /* users erin gooien */
            Element usersTable = doc.select("#users").first();
            for (int i = 0; i < usrdata.size(); i++)
                usersTable.append(String.format("<tr><td>" +
                        "<form action='users.html' method='post' class='form-inline'>" +
                        "ID: <input type='text' value='%s' disabled='disabled' class='input-small'>" +
                        " Username: <input type='text' name='username' value='%s' class='input-small'>" +
                        "<input type='submit' name='update' value='Update' class='btn-warning'>" +
                        "<input type='submit' name='delete' value='Delete' class='btn-danger'>" +
                        "</form>" +
                        "</td><tr>", usrdata.get(i)[0], usrdata.get(i)[1]));

            /* wijzigingen opslaan naar de temp file */
            PrintWriter writer = new PrintWriter(ft, "UTF-8");
            writer.write(doc.html());
            writer.close();

            /* bestand terug sturen */
            sendResponse(ft.getPath());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void handleGETsettingsRequest()
    {
        try {
            /* stop de huidige settings in een kopie van settings.html */
            File ft = File.createTempFile("tempsettings", ".tmp");
            File settings = new File(Settings.controlWebRoot + "/settings.html");
            Files.copy(settings.toPath(), ft.toPath(), StandardCopyOption.REPLACE_EXISTING);

            /* we gebruiken jsoup */
            Document doc = Jsoup.parse(ft, "UTF-8"); // inladen

            /* huidige settings erin gooien */
            Elements webport = doc.select("#inputWebPort");
            webport.get(0).attr("value", Integer.toString(Settings.webPort));
            Elements controlport = doc.select("#inputControlPort");
            controlport.get(0).attr("value", Integer.toString(Settings.controlPort));
            Elements webroot = doc.select("#inputWebroot");
            webroot.get(0).attr("value", Settings.webRoot);
            Elements defpage = doc.select("#inputDefaultPage");
            defpage.get(0).attr("value", Settings.defaultPage);
            Elements dirbrowse = doc.select("#inputDirectoryBrowsing");
            dirbrowse.get(0).attr("value", Boolean.toString(Settings.directoryBrowsing));

            /* wijzigingen opslaan naar de temp file */
            PrintWriter writer = new PrintWriter(ft, "UTF-8");
            writer.write(doc.html());
            writer.close();

            /* bestand terug sturen */
            sendResponse(ft.getPath());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                    handleGETsettingsRequest();
                    //sendResponse(Settings.controlWebRoot + "/settings.html");
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

    @Override
    protected boolean isAuthenticated(String pageURL)
    {
        return true;
    }
}
