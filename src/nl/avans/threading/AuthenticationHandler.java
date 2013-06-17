package nl.avans.threading;

import nl.avans.threading.Logging.Logger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal Slegtenhorst
 * Date: 16-6-13
 * Time: 17:32
 */
public class AuthenticationHandler
{
    private static AuthenticationHandler _instance;

    private final int SECURITYLEVEL_ADMIN = 0;
    private final int SECURITYLEVEL_SUPERUSER = 1;
    private final int SECURITYLEVEL_USER = 2;
    public  final int MAX_SESSION_AGE = 300000; //300.000ms == 5 minutes

    private Hashtable<String, SessionData> sessionStorage; //SessionStorage is never cleared

    private AuthenticationHandler()
    {
        sessionStorage = new Hashtable<String, SessionData>();
    }

    public static AuthenticationHandler getInstance()
    {
        if (_instance == null)
            _instance = new AuthenticationHandler();
        return _instance;
    }

    private String nextSessionId()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    /*
    *  Checks if users session is valid
    */
    private boolean isValidSession(int userId, String sessionId, int requiredSecurityLevel)
    {
        if (sessionId == null)
            return false;
        else {
            SessionData userSessionData = sessionStorage.get(sessionId);
            if (userSessionData == null) //sessionId cannot be found in storage
                return false;
            if (requiredSecurityLevel == userSessionData.securityLevel) //user has no privilege to access a page
                return false;
            long currentTimestamp = System.currentTimeMillis();
            if ((currentTimestamp - userSessionData.timeStampCreation) > MAX_SESSION_AGE) //session has expired
                return false;
            if (userSessionData.userId != userId) //sessionId does not belong to current user
                return false;
            return true; //everything is ok!
        }
    }

    /*
    *  Create a session-id for a specific user and stores it in memory
    *  @return session-id hash
    */
    public String setSession(String userName)
    {
        Logger logger = Logger.getInstance();
        //DataIOHandler dataIOHandler = DataIOHandler.getInstance();
        //Create random session hash
        String sessionId = nextSessionId();

        //Create session information data object
        SessionData userSessionData = new SessionData();
        userSessionData.userId = 1; //TODO get from DataIOHandler
        userSessionData.securityLevel = 1; //TODO get from DataIOHandler
        userSessionData.timeStampCreation = System.currentTimeMillis();

        //add session to storage
        sessionStorage.put(sessionId, userSessionData);

        //log action
        logger.LogMessage("New session created for user: '" + userName + "' and sessionId: '" + sessionId + "'");

        return sessionId;
    }

    private class SessionData
    {
        public int userId;
        public int securityLevel;
        public long timeStampCreation;
    }
}
