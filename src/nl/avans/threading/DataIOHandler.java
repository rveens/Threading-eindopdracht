package nl.avans.threading;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.security.*;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal Slegtenhorst
 * Date: 28-5-13
 * Time: 22:17
 */
public class DataIOHandler {

    private static DataIOHandler _instance;
    Connection dbConnection;

    //CONFIGURATION//
    //String dbUrl = "jdbc:mysql://localhost:3306/";
    //String dbTable = "webservAuth";
    //String dbUsername = "root";
    //String dbPassword = "fDb4tcwT38Dd";

    private DataIOHandler()
    {
        //next statement for testing purposes only
        //System.out.println(verifyUserPassword("pascal", "q83uoq8ydro3c"));
    }

    public static DataIOHandler getInstance()
    {
        if (_instance == null)
            _instance = new DataIOHandler();
        return _instance;
    }

    /*
    *  Verifies if password is equal to password stored in DB
    */
    public boolean verifyUserPassword(String username, String password)
    {
        String passwordHashFromGivenCredentials = hashPassword(username, password);
        String passwordHashFromDB = getUserCredentials(username)[2];
        System.out.println(passwordHashFromGivenCredentials); //for testing purposes only
        System.out.println(passwordHashFromDB);               //for testing purposes only
        return passwordHashFromDB != null && passwordHashFromDB.equals(passwordHashFromGivenCredentials);
    }

    /*
    *  @return all fields of user (from DB) as string-array: ["id", "username", "password"]
    */
    private String[] getUserCredentials(String username)
    {
        String[] result = null;
        try {
            dbConnection = DriverManager.getConnection(Settings.dbUrl + Settings.dbName, Settings.dbUsername, Settings.dbPassword);
            PreparedStatement prepStatement = dbConnection.prepareStatement("SELECT * FROM users where name = (?)");
            prepStatement.setString(1, username);
            ResultSet resultSet = prepStatement.executeQuery();

            if (resultSet.next()) {
                result = new String[3];
                result[0] = resultSet.getString(1); //ID
                result[1] = resultSet.getString(2); //NAME
                result[2] = resultSet.getString(3); //PASSWORD
                //for (String s : result)
                //    System.out.println(s);
            }
            dbConnection.close();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        if (result == null)
            result = new String[]{"", "", ""}; //not nice
        return result;
    }

    /*
    *  @return a hash of the given password with some salt and pepper
    */
    private String hashPassword(String username, String password)
    {
        String pepper = "gHh!7W8@3gHis&dSg8ksTg#";
        String salt = hash(username);
        return hash(hash(salt + hash(password)) + pepper);
    }

    /*
    *  @return SHA-256 hash of given string
    */
    private String hash(String hashable)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(hashable.getBytes());
            return HexBin.encode(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); //WILL NEVER HAPPEN
            return null;
        }
    }
}
