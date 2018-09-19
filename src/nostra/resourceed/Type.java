package nostra.resourceed;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Type
{
    private ReadOnlyIntegerWrapper id;
    
    private StringProperty name;
    
    public Type(final int id, String name)
    {
        this.id = new ReadOnlyIntegerWrapper(id);
        this.name = new SimpleStringProperty(name);
    }
    
    public int getId()
    {
        return id.get();
    }
    
    public ReadOnlyIntegerProperty idProperty()
    {
        return id;
    }
    
    public String getName()
    {
        return name.get();
    }
    
    public void setName(String name)
    {
        this.name.set(name);
    }
    
    public StringProperty nameProperty()
    {
        return name;
    }
}
