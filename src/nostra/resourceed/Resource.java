package nostra.resourceed;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Resource
{
    private Editor editor;
    
    private ReadOnlyIntegerWrapper id;
    
    private StringProperty path;
    
    private StringProperty cached;
    
    private IntegerProperty typeId;
    
    public Resource(Editor editor, final int id, String path, String cached, int typeId)
    {
        this.editor = editor;
        this.id = new ReadOnlyIntegerWrapper(id);
        this.path = new SimpleStringProperty(path);
        this.cached = new SimpleStringProperty(cached);
        this.typeId = new SimpleIntegerProperty(typeId);
    }
    
    public int getId()
    {
        return id.get();
    }
    
    public ReadOnlyIntegerProperty idProperty()
    {
        return id;
    }
    
    public String getPath()
    {
        return path.get();
    }
    
    public void setPath(String path)
    {
        this.path.set(path);
    }
    
    public StringProperty pathProperty()
    {
        return path;
    }

    public String getCached()
    {
        return cached.get();
    }
    
    public void setCached(String path)
    {
        this.cached.set(path);
    }
    
    public StringProperty cachedProperty()
    {
        return cached;
    }
    
    public int getTypeId()
    {
        return typeId.get();
    }
    
    public Type getType()
    {
        return editor.getType(getTypeId());
    }
    
    public void setTypeId(int typeId)
    {
        this.typeId.set(typeId);
    }
    
    public void setType(Type type)
    {
        setTypeId(type.getId());
    }
    
    public IntegerProperty typeIdProperty()
    {
        return typeId;
    }
}
