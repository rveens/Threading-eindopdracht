package nl.avans.threading.Servers;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 6/17/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class RestartWebServerRunTimeException extends RuntimeException
{
    public RestartWebServerRunTimeException(String mes)
    {
        super(mes);
    }
}
