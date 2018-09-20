package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Resource
{
    private Editor editor;
    
    private final int id;
    
    public Resource(Editor editor, final int id)
    {
        this.editor = editor;
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }

    public String getPath()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(Editor.RESOURCE_PATH_COLUMN)
                                    .from(Editor.RESOURCE_TABLE)
                                    .where(Editor.RESOURCE_ID_COLUMN, getId())
                                    .executeQuery();
        
        try
        {
            boolean hasNext = result.next();
            
            String ret = null;
            
            if(hasNext)
                ret = result.getString(1);
            else //this case should never happen if the instance was constructed by Editor.getResource()
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
    
    public boolean setPath(String path)
    {
        //TODO: errors like this should be handled by the database, but the interface does not support that yet
        if(path == null)
            throw new NullPointerException("The path must not be null.");
        
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        int affectedRows = builder.update(Editor.RESOURCE_TABLE)
                .set(Editor.RESOURCE_PATH_COLUMN, path)
                .where(Editor.RESOURCE_ID_COLUMN, getId())
                .executeUpdate();
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }

    public String getCache()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(Editor.RESOURCE_CACHED_COLUMN)
                                    .from(Editor.RESOURCE_TABLE)
                                    .where(Editor.RESOURCE_ID_COLUMN, getId())
                                    .executeQuery();
        
        try
        {
            boolean hasNext = result.next();
            
            String ret = null;
            
            if(hasNext)
                ret = result.getString(1);
            else //this case should never happen if the instance was constructed by Editor.getResource()
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
    
    public boolean setCached(String cache)
    {
        //TODO: errors like this should be handled by the database, but the interface does not support that yet
        if(cache == null)
            throw new NullPointerException("The cache must not be null.");
        
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        int affectedRows = builder.update(Editor.RESOURCE_TABLE)
                .set(Editor.RESOURCE_CACHED_COLUMN, cache)
                .where(Editor.RESOURCE_ID_COLUMN, getId())
                .executeUpdate();
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }

    public int getTypeId()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(Editor.RESOURCE_TYPE_COLUMN)
                                    .from(Editor.RESOURCE_TABLE)
                                    .where(Editor.RESOURCE_ID_COLUMN, getId())
                                    .executeQuery();
        
        try
        {
            boolean hasNext = result.next();
            
            int ret = 0; //the IDs start at 1
            
            if(hasNext)
                ret = result.getInt(1);
            else //this case should never happen if the instance was constructed by Editor.getResource()
                ret = 0;
            
            result.close();
            
            return ret;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return 0;
        }
    }
    
    public Type getType()
    {
        return new Type(editor, getTypeId());
    }
    
    public boolean setType(int typeId)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        int affectedRows = builder.update(Editor.RESOURCE_TABLE)
                .set(Editor.RESOURCE_TYPE_COLUMN, typeId)
                .where(Editor.RESOURCE_ID_COLUMN, getId())
                .executeUpdate();
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public boolean setType(Type type)
    {
        return setType(type.getId());
    }
    
    public Editor getEditor()
    {
        return editor;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        
        if(obj instanceof Resource)
        {
            Resource type = (Resource) obj;
            
            return getId() == type.getId();
        }
        else
            return false;
    }
    
    @Override
    public String toString()
    {
        return "Resource(id=" + getId() + ";path=" + getPath() + ";cache=" + getCache() + ";type=" + getTypeId() + ")";
    }
}
