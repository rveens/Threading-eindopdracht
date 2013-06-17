package nl.avans.threading.Requesthandling;

import nl.avans.threading.*;
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
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 9-6-13
 * Time: 13:11
 */
public class ControlServerRequestHandler extends RequestHandler {

    private String sessionHeader;

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
            /* per user */
            for (int i = 0; i < usrdata.size(); i++)
                usersTable.append(String.format("<tr><td>" +
                        "<form action='users.html' method='post' class='form-inline'>" +
                        "ID: <input type='text' name='ID' value='%s' readonly='readonly' class='input-small'>" +
                        " Username: <input type='text' name='username' value='%s' class='input-small'>" +
                        "<label class='checkbox'>" +
                        "Admin <input name='isAdmin' %s type='checkbox'>" +
                        "</label>" +
                        "<input type='submit' name='update' value='Update' class='btn-warning'>" +
                        "<input type='submit' name='delete' value='Delete' class='btn-danger'>" +
                        "</form>" +
                        "</td><tr>", usrdata.get(i)[0], usrdata.get(i)[1], usrdata.get(i)[2].equals("0") ? "" : "checked") );

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
            if (Settings.directoryBrowsing)
                dirbrowse.get(0).attr("checked", "");

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
        if (reqparser.getUrl().equals("/"))  { // login page
            handleLoginPOSTRequest();
        } else if (reqparser.getUrl().equals("/users.html")) {
            handleUsersPOSTRequest();
        }
    }

    private void handleUsersPOSTRequest()
    {
        Hashtable<String, String> contentBody = reqparser.getContentBody();
        DataIOHandler datahandler = DataIOHandler.getInstance();

        if (contentBody != null) {
            /* kijk of we alle waardes hebben... */
            if (contentBody.get("username") == null ||
                    contentBody.get("isAdmin") == null ||
                    contentBody.get("ID") == null) {
                // TODO THROW UP AN ERROR PAGE
            }
            if (contentBody.get("update") != null) { // handle update
                if (contentBody.get("isAdmin") != null && contentBody.get("isAdmin").equals("on") )
                    datahandler.UpdateUser(Integer.parseInt(contentBody.get("ID")), contentBody.get("username"), true);
                else
                    datahandler.UpdateUser(Integer.parseInt(contentBody.get("ID")), contentBody.get("username"), false);
                // TODO handle failed
                handleGETusersRequest();
            } else if (contentBody.get("delete") != null) { // handle delete
                // TODO handle failed
                datahandler.DestroyUser(Integer.parseInt(contentBody.get("ID")));
                handleGETusersRequest();
            } else if (contentBody.get("new") != null) { // handle create
                // TODO handle failed
                datahandler.CreateUser();
                handleGETusersRequest();
            } else {
                // TODO THROW UP AN ERROR PAGE
            }
        }
    }

    private void handleLoginPOSTRequest() throws HTTPInvalidRequestException {
        Hashtable<String, String> contentBody = reqparser.getContentBody();
        if (contentBody != null) {
            if (contentBody.get("username") != null) { // check if post came from login-page
                if (handleLoginFormData(contentBody)) {
                    logger.LogMessage("Login attempt succeeded");
                    buildSessionHeader(contentBody.get("username"));
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
            // TODO THROW UP AN ERROR PAGE
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
                contentBody.get("directorybrowsing") != null));
    }

    @Override
    protected boolean isAuthenticated(String pageURL)
    {
        // only for <webroot>/login.html and <webroot>/css and <webroot>/js and <webroot>/img is no authentication needed
        return true;//(pageURL.equals(Settings.controlWebRoot + "/login.html") || pageURL.contains(Settings.controlWebRoot + "/css/") || pageURL.contains(Settings.controlWebRoot + "/js/"));
    }

    @Override
    protected String getSessionHeader()
    {
        if (sessionHeader != null)
            return sessionHeader;
        else
            return "";
    }

    private void buildSessionHeader(String username)
    {
        AuthenticationHandler authHandler = AuthenticationHandler.getInstance();
        final String secureCookiePart = "; Expires= " + WebserverConstants.DATE_FORMAT_RESPONSE.format(System.currentTimeMillis() + authHandler.MAX_SESSION_AGE) + "; Secure; HttpOnly";
        sessionHeader = "Set-Cookie: userId=1" + secureCookiePart + "\n";
        sessionHeader += "Set-Cookie: sessionId=" + authHandler.setSession(username) + secureCookiePart;
    }


}
