package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Resource
{
    /** Represents the Resource Table name inside the database. */
    public static final String SQL_TABLE = "Resources";
    /** Represents the Resource Id column name inside the database. */
    public static final String SQL_COL_ID = "ID";
    /** Represents the Resource Path column name inside the database. */
    public static final String SQL_COL_PATH = "path";
    /** Represents the Resource Cache column name inside the database. */
    public static final String SQL_COL_CACHED = "cached";
    /** Represents the Resource Type column name inside the database.  */
    public static final String SQL_COL_TYPE = "TypesID";

    public final static String SQL_CREATE_TABLE  =
            "CREATE TABLE IF NOT EXISTS `" + SQL_TABLE + "` (" + 
            "   `" + SQL_COL_ID     + "` INTEGER NOT NULL," + 
            "   `" + SQL_COL_PATH   + "` TEXT NOT NULL CHECK(" + SQL_COL_PATH + " NOT LIKE " + SQL_COL_CACHED + ") UNIQUE," + 
            "   `" + SQL_COL_CACHED + "` TEXT CHECK(" + SQL_COL_CACHED + " NOT LIKE 'NULL')," + 
            "   `" + SQL_COL_TYPE   + "` INTEGER NOT NULL," + 
            "   PRIMARY KEY(`" + SQL_COL_ID + "`)," + 
            "   FOREIGN KEY(`" + SQL_COL_TYPE + "`) REFERENCES `" + Type.SQL_TABLE + "`(`" + Type.SQL_COL_ID + "`) ON DELETE NO ACTION ON UPDATE CASCADE);";
    
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
        
        ResultSet result = builder.select(SQL_COL_PATH)
                                    .from(SQL_TABLE)
                                    .where(SQL_COL_ID, getId())
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
        
        int affectedRows = builder.update(SQL_TABLE)
                .set(SQL_COL_PATH, path)
                .where(SQL_COL_ID, getId())
                .executeUpdate();
        
        if(affectedRows == 1)
            editor.fireResourceEditEvent(this);
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }

    public String getCache()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(SQL_COL_CACHED)
                                    .from(SQL_TABLE)
                                    .where(SQL_COL_ID, getId())
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
        
        int affectedRows = builder.update(SQL_TABLE)
                .set(SQL_COL_CACHED, cache)
                .where(SQL_COL_ID, getId())
                .executeUpdate();

        if(affectedRows == 1)
            editor.fireResourceEditEvent(this);
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }

    public int getTypeId()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(SQL_COL_TYPE)
                                    .from(SQL_TABLE)
                                    .where(SQL_COL_ID, getId())
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
        
        int affectedRows = builder.update(SQL_TABLE)
                .set(SQL_COL_TYPE, typeId)
                .where(SQL_COL_ID, getId())
                .executeUpdate();

        if(affectedRows == 1)
            editor.fireResourceEditEvent(this);
        
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
    
    public List<Group> getGroups()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        ResultSet result = builder.select(Editor.GROUPS_RESOURCES_SQL_COL_GROUP_ID)
                                    .from(Editor.GROUPS_RESOURCES_SQL_TABLE)
                                    .where(Editor.GROUPS_RESOURCES_SQL_COL_RESOURCE_ID, getId())
                                    .executeQuery();
        
        try
        {
            List<Group> ret = new ArrayList<Group>();
            
            while(result.next())
            {
                ret.add(new Group(editor, result.getInt(1)));
            }
            
            result.close();
            
            return ret;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return new ArrayList<Group>();
        }
    }
    
    public boolean addToGroup(int groupId)
    {
        Group group = editor.getGroup(groupId);
        
        if(group != null)
            return group.addMember(getId());
        else
            return false; //group does not even exist
    }

    public boolean addToGroup(Group group)
    {
        return addToGroup(group.getId());
    }

    public boolean removeFromGroup(int groupId)
    {
        Group group = editor.getGroup(groupId);
        
        if(group != null)
            return group.removeMember(getId());
        else
            return false; //group does not even exist
    }

    public boolean removeFromGroup(Group group)
    {
        return removeFromGroup(group.getId());
    }

    public boolean isMemberOf(int groupId)
    {
        Group group = editor.getGroup(groupId);
        
        if(group != null)
            return group.isMember(getId());
        else
            return false; //group does not even exist
    }

    public boolean isMemberOf(Group group)
    {
        return isMemberOf(group.getId());
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
