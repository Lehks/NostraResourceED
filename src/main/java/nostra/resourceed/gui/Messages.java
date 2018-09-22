package nostra.resourceed.gui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
    private static final String BUNDLE_NAME = "nostra.resourceed.gui.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages()
    {
    }

    public static String get(String key)
    {
        try
        {
            return RESOURCE_BUNDLE.getString(key);
        } 
        catch (MissingResourceException e)
        {
            return '$' + key + '$';
        }
    }
}
