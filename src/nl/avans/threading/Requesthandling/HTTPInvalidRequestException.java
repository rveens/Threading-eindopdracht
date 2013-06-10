package nl.avans.threading.Requesthandling;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/29/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPInvalidRequestException extends Exception
{
    private int responseCode;

    public HTTPInvalidRequestException(int responseCode, String message)
    {
        super(message);
        this.responseCode = responseCode;
    }

    public int getResponseCode()
    {
        return responseCode;
    }
}
