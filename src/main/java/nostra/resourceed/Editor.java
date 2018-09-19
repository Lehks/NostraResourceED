package nostra.resourceed;

import java.io.File;

public class Editor
{
    public Editor(String databasePath)
    {
        this(new File(databasePath));
    }
    
    public Editor(File database)
    {
        
    }
    
    Resource getResource(int id)
    {
        return null;
    }
    
    Type getType(int id)
    {
        return null;
    }
    
    Resource addResource(String name, String cached, int typeId)
    {
        return null;
    }
    
    Resource addResource(String name, String cached, Type type)
    {
        return addResource(name, cached, type.getId());
    }
    
    Type addType(String name)
    {
        return null;
    }
}
