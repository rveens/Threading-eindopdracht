package nl.avans.threading;

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
}
