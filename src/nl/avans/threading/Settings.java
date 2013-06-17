package nl.avans.threading;

import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal Slegtenhorst
 * Date: 30-5-13
 * Time: 20:50
 */
public class Settings {
    //DB-settings//
    public static String dbUrl = "";
    public static String dbName = "";
    public static String dbUsername = "";
    public static String dbPassword = "";

    //Server-settings//
    public static int webPort = 0;
    public static int controlPort = 0;
    public static String webRoot = "";
    public static String controlWebRoot = "";
    public static String defaultPage = "";
    public static String defaultControlPage = "";
    public static String logLocation = "log.txt";
    public static String SettingsFileLocation = "settings.xml";
    public static boolean directoryBrowsing = false;

    //Dictionary for looking up the security level for a specific page on GET Request
    public static Hashtable<String, Integer> authorisationLookupGETReq;
    static {
        authorisationLookupGETReq = new Hashtable<String, Integer>();
        authorisationLookupGETReq.put("/", 2);
        authorisationLookupGETReq.put("/login.html", 2);
        authorisationLookupGETReq.put("/settings.html", 1);
        authorisationLookupGETReq.put("/logs", 1);
        //authorisationLookupGETReq.put("/users.html", 0); //this is commented out, to check if '.users.html' has level 0 as default
    }

    //Dictionary for looking up the security level for a specific page on POST Request
    public static Hashtable<String, Integer> authorisationLookupPOSTReq;
    static {
        authorisationLookupPOSTReq = new Hashtable<String, Integer>();
        authorisationLookupPOSTReq.put("/", 2);
        authorisationLookupPOSTReq.put("/login.html", 2);
        authorisationLookupPOSTReq.put("/users.html", 0);
        authorisationLookupPOSTReq.put("/settings.html", 0);
    }
}
