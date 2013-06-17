package nl.avans.threading;

import nl.avans.threading.Logging.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal Slegtenhorst
 * Date: 30-5-13
 * Time: 20:49
 */
public class SettingsIOHandler {

    private static Properties properties;

    /**
     *  Read the settings from settings.xml document
     *   and store values in static Settings class
     */
    public static void loadSettings()
    {
        //handle loading properties file
        handlePropertyLoading();
        try {
            //get the properties value and specify default value when property not available
            Settings.dbUrl = properties.getProperty("db-url", "jdbc:mysql://localhost:3306/");
            Settings.dbName = properties.getProperty("db-name", "webservAuth");
            Settings.dbUsername = properties.getProperty("db-username", "root");
            Settings.dbPassword = properties.getProperty("db-password", "fDb4tcwT38Dd");

            Settings.webPort = Integer.parseInt(properties.getProperty("web-port", "8080"));
            Settings.controlPort = Integer.parseInt(properties.getProperty("control-port", "8081"));
            Settings.webRoot = properties.getProperty("web-root", "htdocs");
            Settings.controlWebRoot = properties.getProperty("control-web-root", "htdocs");
            Settings.defaultPage = properties.getProperty("default-page", "index.html");
            Settings.defaultControlPage = properties.getProperty("default-control-page", "login.html");
            Settings.logLocation = properties.getProperty("log-location", "log.txt");
            Settings.directoryBrowsing = Boolean.valueOf(properties.getProperty("directoryBrowsing", "false"));

        } catch (Exception e) {
            System.out.println("ERROR settings.xml document is not wel formed");
        }
    }

    /**
     *  Store values from @param in settings.xml file
     */
    public static boolean saveChanges(int webPort, int controlPort, String webRoot, String defaultPage, boolean dirBrowsing)
    {
        handlePropertyLoading();
        if (!(webPort > 79 && webPort < 10000))
            return false;
        if (!(controlPort > 79 && controlPort < 10000))
            return false;
        if (webRoot == null && defaultPage == null)
            return false;
        //TODO check if webroot and defaultpage is valid

        try {
            webRoot = encodeParam(webRoot);
            defaultPage = encodeParam(defaultPage);

            //set the properties value
            properties.setProperty("web-port", "" + webPort);
            properties.setProperty("control-port", "" + controlPort);
            properties.setProperty("web-root", webRoot);
            properties.setProperty("default-page", defaultPage);
            properties.setProperty("directoryBrowsing", dirBrowsing ? "true" : "false");

            //save properties to project root folder
            properties.storeToXML(new FileOutputStream(Settings.SettingsFileLocation), null);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
    *   Encode parameter to prevent xml-injection
    */
    private static String encodeParam(String param) throws Exception {
        //DONE prevent xml-injection by escaping characters like '<' and "'"
        if (!(param == null)) {
            if (!param.matches("^[a-zA-Z0-9-/]+$")) //white listing
                throw new Exception("Possible injection detected");
        }
        return param;
    }

    private static void handlePropertyLoading()
    {
        if (properties == null) {
            properties = new Properties();
            try {
                //load properties file
                properties.loadFromXML(new FileInputStream(Settings.SettingsFileLocation));

            } catch (IOException e) {
                e.printStackTrace();
                Logger.getInstance().LogMessage("ERROR: Cannot find file: 'new-settings.xml'. Default settings loaded");
            }
        }
    }
}