package nl.avans.threading.Requesthandling;

import nl.avans.threading.Settings;
import nl.avans.threading.SettingsIOHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.xml.bind.Element;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        } else if (reqparser.getUrl().equals("/log"))
            sendResponse(Settings.logLocation);
        else if (reqparser.getUrl().equals("/settings.html")) {
            /* stop de huidige settings in een kopie van settings.html */
            handleGETsettingsRequest();
        } else {
            sendResponse(Settings.controlWebRoot + reqparser.getUrl());
        }
    }

    private void handleGETsettingsRequest()
    {
        try {
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
