package nl.avans.threading;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal Slegtenhorst
 * Date: 30-5-13
 * Time: 20:50
 */
public class Settings {
    //DB-settings//
    public static String dbUrl = "jdbc:mysql://localhost:3306/";
    public static String dbName = "webservAuth";
    public static String dbUsername = "root";
    public static String dbPassword = "fDb4tcwT38Dd";

    //Server-settings//
    public static int webPort = 80;
    public static int controlPort = 81;
    public static String webRoot = "";
    public static String defaultPage = "";
    public static String logLocation = "log.txt";
}
