package nostra.resourceed;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Editor
{
    /** Represents the Type table name inside the database. */
    public static final String TYPE_TABLE = "resourceType";
    /** Represents the Type Id column name inside the database. */
    public static final String TYPE_ID_COLUMN = "tid";
    /** Represents the Type Name column name inside the database */
    public static final String TYPE_NAME_COLUMN = "name";

    /** Represents the Resource Table name inside the database. */
    public static final String RESOURCE_TABLE = "resource";
    /** Represents the Resource Id column name inside the database. */
    public static final String RESOURCE_ID_COLUMN = "rid";
    /** Represents the Resource Path column name inside the database. */
    public static final String RESOURCE_PATH_COLUMN = "path";
    /** Represents the Resource Cache column name inside the database. */
    public static final String RESOURCE_CACHED_COLUMN = "cached";
    /** Represents the Resource Type column name inside the database.  */
    public static final String RESOURCE_TYPE_COLUMN = "type";

    /** Represents the Group Table name inside the database. */
    public static final String GROUP_TABLE = "resourceGroup";
    /** Represents the Group Name column name inside the database. */
    public static final String GROUP_NAME_COLUMN = "name";
    /** Represents the Group Id column name inside the database. */
    public static final String GROUP_ID_COLUMN = "gid";

    /** Represents the Grouped Table name inside the database. */
    public static final String GROUPED_TABLE = "resourceGroups";
    /** Represents the Grouped ResourceId column name inside the database. */
    public static final String GROUPED_RESOURCE_ID_COLLUMN = "resourceID";
    /** Represents the Grouped GroupId column name inside the database. */
    public static final String GROUPED_GROUP_ID_COLLUMN = "groupID";

    
    /**
     * The database to edit.
     */
    private Database database;
    
    public Editor(String databasePath)
    {
        this(new File(databasePath));
    }
    
    public Editor(File database)
    {
        
    }
    
    public Editor(Database database)
    {
        this.database = database;
    }
    
    public Database getDatabase()
    {
        return database;
    }
    
    public Resource getResource(int id)
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(RESOURCE_ID_COLUMN)
                                    .from(RESOURCE_TABLE)
                                    .where(RESOURCE_ID_COLUMN, id)
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
            boolean hasNext = result.next();
            result.close();
            
            if(hasNext)
                return new Resource(this, id);
            else
                return null;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return null;
        }
    }
    
    public Type getType(int id)
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(TYPE_ID_COLUMN)
                                    .from(TYPE_TABLE)
                                    .where(TYPE_ID_COLUMN, id)
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
            boolean hasNext = result.next();
            result.close();
            
            if(hasNext)
                return new Type(this, id);
            else
                return null;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return null;
        }
    }

    public Group getGroup(int id)
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(GROUP_ID_COLUMN)
                                    .from(GROUP_TABLE)
                                    .where(GROUP_ID_COLUMN, id)
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
            boolean hasNext = result.next();
            result.close();
            
            if(hasNext)
                return new Group(this, id);
            else
                return null;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return null;
        }
    }
    
    public Resource addResource(String path, String cached, int typeId)
    {
        //TODO: errors like this should be handled by the database, but the interface does not support that yet
        if(path == null)
            throw new NullPointerException("The path must not be null.");
        if(cached == null)
            throw new NullPointerException("The cache must not be null."); //TODO: cache sometimes needs to be ŃULL!

        QueryBuilder query = new QueryBuilder(database);
        
        int affectedRows = query.insert(RESOURCE_TABLE)
            .set(RESOURCE_PATH_COLUMN, path)
            .set(RESOURCE_CACHED_COLUMN, cached)
            .set(RESOURCE_TYPE_COLUMN, typeId)
            .executeUpdate();
        
        //affected rows is 0, nothing was inserted
        if(affectedRows == 0)
            return null;
        else 
            return new Resource(this, query.getInsertId());
    }
    
    public Resource addResource(String path, String cached, Type type)
    {
        return addResource(path, cached, type.getId());
    }
    
    public Type addType(String name)
    {
        //TODO: errors like this should be handled by the database, but the interface does not support that yet
        if(name == null)
            throw new NullPointerException("The name must not be null.");
        
        QueryBuilder query = new QueryBuilder(database);
        
        int affectedRows = query.insert(TYPE_TABLE)
            .set(TYPE_NAME_COLUMN, name)
            .executeUpdate();
        
        //affected rows is 0, nothing was inserted
        if(affectedRows == 0)
            return null;
        else 
            return new Type(this, query.getInsertId());
    }

    public Group addGroup(String name)
    {
        //TODO: errors like this should be handled by the database, but the interface does not support that yet
        if(name == null)
            throw new NullPointerException("The name must not be null.");
        
        QueryBuilder query = new QueryBuilder(database);
        
        int affectedRows = query.insert(GROUP_TABLE)
            .set(GROUP_NAME_COLUMN, name)
            .executeUpdate();
        
        //affected rows is 0, nothing was inserted
        if(affectedRows == 0)
            return null;
        else 
            return new Group(this, query.getInsertId());
    }
    
    public List<Resource> getResources()
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(Editor.RESOURCE_ID_COLUMN)
                                    .from(Editor.RESOURCE_TABLE)
                                    .executeQuery();
        
        try
        {
            List<Resource> ret = new ArrayList<Resource>();
            
            while(result.next())
            {
                ret.add(new Resource(this, result.getInt(1)));
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
    
    public List<Type> getTypes()
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(Editor.TYPE_ID_COLUMN)
                                    .from(Editor.TYPE_TABLE)
                                    .executeQuery();
        
        try
        {
            List<Type> ret = new ArrayList<Type>();
            
            while(result.next())
            {
                ret.add(new Type(this, result.getInt(1)));
            }
            
            result.close();
            
            return ret;
        } 
        catch (SQLException e)
        {
            //should never happen
            e.printStackTrace();
            return new ArrayList<Type>();
        }
    }
    
    public List<Group> getGroups()
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(Editor.GROUP_ID_COLUMN)
                                    .from(Editor.GROUP_TABLE)
                                    .executeQuery();
        
        try
        {
            List<Group> ret = new ArrayList<Group>();
            
            while(result.next())
            {
                ret.add(new Group(this, result.getInt(1)));
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
    
    public boolean removeResource(int resourceId)
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        int affectedRows = builder
                .delete()
                .from(Editor.RESOURCE_TABLE)
                .where(Editor.RESOURCE_ID_COLUMN, resourceId)
                .executeUpdate();

        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public boolean removeResource(Resource resource)
    {
        return removeResource(resource.getId());
    }

    public boolean removeType(int typeId)
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        int affectedRows = builder
                .delete()
                .from(Editor.TYPE_TABLE)
                .where(Editor.TYPE_ID_COLUMN, typeId)
                .executeUpdate();

        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public boolean removeType(Type type)
    {
        return removeType(type.getId());
    }

    public boolean removeGroup(int groupId)
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        int affectedRows = builder
                .delete()
                .from(Editor.GROUP_TABLE)
                .where(Editor.GROUP_ID_COLUMN, groupId)
                .executeUpdate();

        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public boolean removeGroup(Group group)
    {
        return removeGroup(group.getId());
    }
}
