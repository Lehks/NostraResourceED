package nostra.resourceed.gui;

/**
 * An exception type that is used by the class FilterSettingsPane in various contexts.
 * 
 * @author Mahan Karimi, Dennis Franz
 */
public class FilterSettingsException extends Exception
{
    private static final long serialVersionUID = -7130579621617109243L;

    public FilterSettingsException()
    {
        super();
    }

    public FilterSettingsException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FilterSettingsException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FilterSettingsException(String message)
    {
        super(message);
    }

    public FilterSettingsException(Throwable cause)
    {
        super(cause);
    }
}
