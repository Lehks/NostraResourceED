package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Group
{
    /** Represents the Group Table name inside the database. */
    public static final String SQL_TABLE = "resourceGroup";
    /** Represents the Group Name column name inside the database. */
    public static final String SQL_COL_NAME = "name";
    /** Represents the Group Id column name inside the database. */
    public static final String SQL_COL_ID = "gid";

    Editor editor;
    
    private final int id;
    
    public Group(Editor editor, final int id)
    {
        this.editor = editor;
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
    
    public String getName()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(SQL_COL_NAME)
                                    .from(SQL_TABLE)
                                    .where(SQL_COL_ID, getId())
                                    .executeQuery();
        
        try
        {
            boolean hasNext = result.next();
            
            String ret = null;
            
            if(hasNext)
                ret = result.getString(1);
            else //this case should never happen if the instance was constructed by Editor.getGroup()
                ret = null;
            
            result.close();
            
            return ret;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean setName(String name)
    {
        //TODO: errors like this should be handled by the database, but the interface does not support that yet
        if(name == null)
            throw new NullPointerException("The name must not be null.");
        
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
                int affectedRows = builder.update(SQL_TABLE)
                .set(SQL_COL_NAME, name)
                .where(SQL_COL_ID, getId())
                .executeUpdate();
        
        if(affectedRows == 1)
            editor.fireGroupEditEvent(this);
                
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public Editor getEditor()
    {
        return editor;
    }
    
    public List<Resource> getMembers()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(Editor.GROUPED_SQL_COL_RESOURCE_ID)
                                    .from(Editor.GROUPED_SQL_TABLE)
                                    .where(Editor.GROUPED_SQL_COL_GROUP_ID, getId())
                                    .executeQuery();
        
        try
        {
            List<Resource> ret = new ArrayList<Resource>();
            
            while(result.next())
            {
                ret.add(new Resource(editor, result.getInt(1)));
            }
            
            result.close();
            
            return ret;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return new ArrayList<Resource>();
        }
    }
    
    public boolean isMember(int resourceId)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        //TODO: optimize with AND (WHERE groupID = id AND resourceID = resourceID)
        ResultSet result = builder.select(Editor.GROUPED_SQL_COL_RESOURCE_ID)
                                    .from(Editor.GROUPED_SQL_TABLE)
                                    .where(Editor.GROUPED_SQL_COL_GROUP_ID, getId())
                                    .executeQuery();
        
        try
        {
            /*
             * Any value in the result set does not matter, because:
             * We only care about if the type exists or not
             * There is either one or none result, since we are querying a primary key value
             * 
             * -> If there is a next, it the key was found; if not it was not found
             */
            //boolean hasNext = result.next();
            //result.close();
            
            //return hasNext;
            
            //the code above only works with the aforementioned optimization
            while(result.next())
            {
                if(result.getInt(1) == resourceId)
                    return true;
            }
            
            return false;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMember(Resource resource)
    {
        return isMember(resource.getId());
    }
    
    public boolean removeMember(int resourceId)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        int affectedRows = builder
                .delete()
                .from(Editor.GROUPED_SQL_TABLE)
                .where(Editor.GROUPED_SQL_COL_RESOURCE_ID + " = " + resourceId //bit of a hack to use AND
                        + " AND " + Editor.GROUPED_SQL_COL_GROUP_ID, getId())
                .executeUpdate();

        if(affectedRows == 1)
            editor.fireResourceRemoveFromGroupEvent(this, editor.getResource(resourceId));
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public boolean removeMember(Resource resource)
    {
        return removeMember(resource.getId());
    }
    
    public boolean addMember(int resourceId)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        int affectedRows = builder
                .insert(Editor.GROUPED_SQL_TABLE)
                .set(Editor.GROUPED_SQL_COL_GROUP_ID, getId())
                .set(Editor.GROUPED_SQL_COL_RESOURCE_ID, resourceId)
                .executeUpdate();

        if(affectedRows == 1)
            editor.fireResourceAddToGroupEvent(this, editor.getResource(resourceId));
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public boolean addMember(Resource resource)
    {
        return addMember(resource.getId());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        
        if(obj instanceof Group)
        {
            Group type = (Group) obj;
            
            return getId() == type.getId();
        }
        else
            return false;
    }
    
    @Override
    public String toString()
    {
        return getName() + " (" + getId() + ")";
    }
}
