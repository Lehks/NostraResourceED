package nostra.resourceed.gui;

/**
 * An exception type that is used by the class FilterPreset in various contexts.
 * 
 * @author Mahan Karimi, Dennis Franz
 */
public class FilterPresetException extends Exception
{
    private static final long serialVersionUID = 818872850965894646L;

    public FilterPresetException()
    {
        super();
    }

    public FilterPresetException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FilterPresetException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FilterPresetException(String message)
    {
        super(message);
    }

    public FilterPresetException(Throwable cause)
    {
        super(cause);
    }

}
