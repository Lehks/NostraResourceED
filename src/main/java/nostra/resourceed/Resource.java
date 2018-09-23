package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Resource
{
    /** 
     * The name of the Resources table in SQL. 
     */
    public static final String SQL_TABLE = "Resources";

    /**
     * The name of the ID column Resources table in SQL. 
     */
    public static final String SQL_COL_ID = "ID";

    /**
     * The name of the path column Resources table in SQL. 
     */
    public static final String SQL_COL_PATH = "path";

    /**
     * The name of the cached column Resources table in SQL. 
     */
    public static final String SQL_COL_CACHED = "cached";

    /**
     * The name of the TypeID column Resources table in SQL. 
     */
    public static final String SQL_COL_TYPE = "TypesID";

    /**
     * The SQL code to create the Resources table.
     */
    // @formatter:off
    public final static String SQL_CREATE_TABLE  =
            "CREATE TABLE IF NOT EXISTS `" + SQL_TABLE + "` (" + 
            "   `" + SQL_COL_ID     + "` INTEGER NOT NULL," + 
            "   `" + SQL_COL_PATH   + "` TEXT NOT NULL CHECK(" + SQL_COL_PATH + " NOT LIKE " + SQL_COL_CACHED + ") UNIQUE," + 
            "   `" + SQL_COL_CACHED + "` TEXT CHECK(" + SQL_COL_CACHED + " NOT LIKE 'NULL')," + 
            "   `" + SQL_COL_TYPE   + "` INTEGER NOT NULL," + 
            "   PRIMARY KEY(`" + SQL_COL_ID + "`)," + 
            "   FOREIGN KEY(`" + SQL_COL_TYPE + "`) REFERENCES `" + Type.SQL_TABLE + "`(`" + Type.SQL_COL_ID + "`) ON DELETE NO ACTION ON UPDATE CASCADE);";
    // @formatter:on

    /**
     * The editor that this resource is in.
     */
    private Editor editor;

    /**
     * The ID of the resource.
     */
    private final int id;

    /**
     * The constructor. Only used by the library itself and not by a user.
     * 
     * @param editor The editor of this resource.
     * @param id     The ID of this type.
     */
    //package private to emulate C++ friends; this constructor should not be used by a user
    Resource(Editor editor, final int id)
    {
        this.editor = editor;
        this.id = id;
    }

    /**
     * Returns the ID of this resource.
     * @return The ID of this resource.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the path of this resource.
     * @return The path of this resource.
     */
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

    /**
     * Sets the path of this type.
     * @param path The new path.
     * @return True, if the path was successfully set, false if not.
     */
    public boolean setPath(String path)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        int affectedRows = builder.update(SQL_TABLE)
                .set(SQL_COL_PATH, path)
                .where(SQL_COL_ID, getId())
                .executeUpdate();
        
        if(affectedRows == 1)
            editor.fireResourceEditEvent(this);
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }

    /**
     * Returns the cache path of this resource.
     * @return The cache path of this resource.
     */
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

    /**
     * Sets the cache path of this type.
     * @param cache The new cache path.
     * @return True, if the cache path was successfully set, false if not.
     */
    public boolean setCached(String cache)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        
        int affectedRows = builder.update(SQL_TABLE)
                .set(SQL_COL_CACHED, cache)
                .where(SQL_COL_ID, getId())
                .executeUpdate();

        if(affectedRows == 1)
            editor.fireResourceEditEvent(this);
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }

    /**
     * Returns the type ID of this resource.
     * @return The type ID of this resource.
     */
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

    /**
     * Returns the type of this resource.
     * @return The type of this resource.
     */
    public Type getType()
    {
        return new Type(editor, getTypeId());
    }

    /**
     * Sets the type ID of this type.
     * @param typeId The new type ID.
     * @return True, if the type ID was successfully set, false if not.
     */
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

    /**
     * Sets the type of this type.
     * @param type The new type.
     * @return True, if the type was successfully set, false if not.
     */
    public boolean setType(Type type)
    {
        return setType(type.getId());
    }

    /**
     * Returns the editor that this type is in.
     * @return The editor.
     */
    public Editor getEditor()
    {
        return editor;
    }
    
    /**
     * Returns the groups that this a member of.
     * @return The groups.
     */
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
    
    /**
     * Adds a resource to a group.
     * @param groupId The group to add to.
     * @return True, if the resource was added to the group.
     */
    public boolean addToGroup(int groupId)
    {
        Group group = editor.getGroup(groupId);
        
        if(group != null)
            return group.addMember(getId());
        else
            return false; //group does not even exist
    }

    /**
     * Adds a resource to a group.
     * @param group The group to add to.
     * @return True, if the resource was added to the group.
     */
    public boolean addToGroup(Group group)
    {
        return addToGroup(group.getId());
    }

    /**
     * Removes a resource from a group.
     * @param groupID The group to remove from.
     * @return True, if the resource was removed from the group.
     */
    public boolean removeFromGroup(int groupId)
    {
        Group group = editor.getGroup(groupId);
        
        if(group != null)
            return group.removeMember(getId());
        else
            return false; //group does not even exist
    }

    /**
     * Removes a resource from a group.
     * @param group The group to remove from.
     * @return True, if the resource was removed from the group.
     */
    public boolean removeFromGroup(Group group)
    {
        return removeFromGroup(group.getId());
    }

    /**
     * Checks if the resource is in a group.
     * @param groupId The group to check.
     * @return True, if the resource is part of the passed group, false if not.
     */
    public boolean isMemberOf(int groupId)
    {
        Group group = editor.getGroup(groupId);
        
        if(group != null)
            return group.isMember(getId());
        else
            return false; //group does not even exist
    }

    /**
     * Checks if the resource is in a group.
     * @param group The group to check.
     * @return True, if the resource is part of the passed group, false if not.
     */
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
