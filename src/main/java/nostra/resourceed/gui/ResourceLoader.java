package nostra.resourceed.gui;

import java.io.InputStream;
import java.net.URL;

public class ResourceLoader
{
    private ResourceLoader()
    {
    }
    
    public static InputStream getStream(String name)
    {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(name);
    }
    
    public static URL getUrl(String name)
    {
        return ResourceLoader.class.getClassLoader().getResource(name);
    }
}
