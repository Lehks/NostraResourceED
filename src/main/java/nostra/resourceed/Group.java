package nostra.resourceed;

import java.util.List;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Group
{
    private ReadOnlyIntegerWrapper id;
    
    private StringProperty name;
    
    public Group(final int id, String name)
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
        return id.getReadOnlyProperty();
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
    
    public List<Integer> getMembers()
    {
        return null;
    }

    public boolean isMember(int resourceId)
    {
        return false;
    }

    public boolean isMember(Resource resource)
    {
        return isMember(resource.getId());
    }
    
    public boolean removeMember(int resourceId)
    {
        return false;
    }
    
    public boolean removeMember(Resource resource)
    {
        return removeMember(resource.getId());
    }
    
    public void addMember(int resourceId)
    {
        
    }
    
    public void addMember(Resource resource)
    {
        addMember(resource.getId());
    }
}
