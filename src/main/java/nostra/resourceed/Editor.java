package nostra.resourceed;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Editor implements Closeable
{
    /** Represents the Grouped Table name inside the database. */
    public static final String GROUPED_SQL_TABLE = "resourceGroups";
    /** Represents the Grouped ResourceId column name inside the database. */
    public static final String GROUPED_SQL_COL_RESOURCE_ID = "resourceID";
    /** Represents the Grouped GroupId column name inside the database. */
    public static final String GROUPED_SQL_COL_GROUP_ID = "groupID";

    
    /**
     * The database to edit.
     */
    private Database database;
    
    private List<Consumer<Resource>> resourceAddEvents;
    private List<Consumer<Resource>> resourceEditEvents;
    private List<Consumer<Integer>> resourceRemoveEvents;

    private List<Consumer<Type>> typeAddEvents;
    private List<Consumer<Type>> typeEditEvents;
    private List<Consumer<Integer>> typeRemoveEvents;

    private List<Consumer<Group>> groupAddEvents;
    private List<Consumer<Group>> groupEditEvents;
    private List<Consumer<Integer>> groupRemoveEvents;

    private List<BiConsumer<Group, Resource>> resourceAddToGroupEvents;
    private List<BiConsumer<Group, Resource>> resourceRemoveFromGroupEvents;
    
    public Editor(String databasePath) throws SQLException
    {
        this(new Database(databasePath));
    }
    
    public Editor(File database) throws SQLException
    {
        this(database.getAbsolutePath());
    }
    
    public Editor(Database database)
    {
        this.database = database;
        this.resourceAddEvents = new ArrayList<>();
        this.resourceEditEvents = new ArrayList<>();
        this.resourceRemoveEvents = new ArrayList<>();
        
        this.typeAddEvents = new ArrayList<>();
        this.typeEditEvents = new ArrayList<>();
        this.typeRemoveEvents = new ArrayList<>();
        
        this.groupAddEvents = new ArrayList<>();
        this.groupEditEvents = new ArrayList<>();
        this.groupRemoveEvents = new ArrayList<>();
        
        this.resourceAddToGroupEvents = new ArrayList<>();
        this.resourceRemoveFromGroupEvents = new ArrayList<>();
    }
    
    public Database getDatabase()
    {
        return database;
    }
    
    public Resource getResource(int id)
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(Resource.SQL_COL_ID)
                                    .from(Resource.SQL_TABLE)
                                    .where(Resource.SQL_COL_ID, id)
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
        
        ResultSet result = builder.select(Type.SQL_COL_ID)
                                    .from(Type.SQL_TABLE)
                                    .where(Type.SQL_COL_ID, id)
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
        
        ResultSet result = builder.select(Group.SQL_COL_ID)
                                    .from(Group.SQL_TABLE)
                                    .where(Group.SQL_COL_ID, id)
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
        
        int affectedRows = query.insert(Resource.SQL_TABLE)
            .set(Resource.SQL_COL_PATH, path)
            .set(Resource.SQL_COL_CACHED, cached)
            .set(Resource.SQL_COL_TYPE, typeId)
            .executeUpdate();
        
        //affected rows is 0, nothing was inserted
        if(affectedRows == 0)
            return null;
        else 
        {
            Resource ret = new Resource(this, query.getInsertId());
            
            resourceAddEvents.forEach(event -> event.accept(ret));

            return ret;
        }
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
        
        int affectedRows = query.insert(Type.SQL_TABLE)
            .set(Type.SQL_COL_NAME, name)
            .executeUpdate();
        
        //affected rows is 0, nothing was inserted
        if(affectedRows == 0)
            return null;
        else 
        {
            Type ret = new Type(this, query.getInsertId());

            typeAddEvents.forEach(event -> event.accept(ret));

            return ret;
        }
    }

    public Group addGroup(String name)
    {
        //TODO: errors like this should be handled by the database, but the interface does not support that yet
        if(name == null)
            throw new NullPointerException("The name must not be null.");
        
        QueryBuilder query = new QueryBuilder(database);
        
        int affectedRows = query.insert(Group.SQL_TABLE)
            .set(Group.SQL_COL_NAME, name)
            .executeUpdate();
        
        //affected rows is 0, nothing was inserted
        if(affectedRows == 0)
            return null;
        else 
        {
            Group ret = new Group(this, query.getInsertId());

            groupAddEvents.forEach(event -> event.accept(ret));

            return ret;
        }
    }
    
    public List<Resource> getResources()
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(Resource.SQL_COL_ID)
                                    .from(Resource.SQL_TABLE)
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

    public List<Resource> getResources(Filter filter)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
            .append(Resource.SQL_COL_ID)
            .append(" FROM ")
            .append(Resource.SQL_TABLE);
        
        //joins
        sql.append(" INNER JOIN ")
            .append(Type.SQL_TABLE)
            .append(" ON ")
            .append(Resource.SQL_TABLE)
            .append(".")
            .append(Resource.SQL_COL_TYPE)
            .append(" = ")
            .append(Type.SQL_TABLE)
            .append(".")
            .append(Type.SQL_COL_ID);
            
        sql.append(" LEFT JOIN ")
            .append(GROUPED_SQL_TABLE)
            .append(" ON ")
            .append(Resource.SQL_TABLE)
            .append(".")
            .append(Resource.SQL_COL_ID)
            .append(" = ")
            .append(GROUPED_SQL_TABLE)
            .append(".")
            .append(GROUPED_SQL_COL_RESOURCE_ID);

        sql.append(" LEFT JOIN ")
            .append(Group.SQL_TABLE)
            .append(" ON ")
            .append(GROUPED_SQL_TABLE)
            .append(".")
            .append(GROUPED_SQL_COL_GROUP_ID)
            .append(" = ")
            .append(Group.SQL_TABLE)
            .append(".")
            .append(Group.SQL_COL_ID);
        
        sql.append(" WHERE ");
        sql.append(filter.generateSQL());
        
        //remove duplicate IDs
        sql.append(" GROUP BY ")
            .append(Resource.SQL_TABLE)
            .append(".")
            .append(Resource.SQL_COL_ID);
        
        PreparedStatement stmt = database.prepQuery(sql.toString());
        
        try
        {
            ResultSet result = stmt.executeQuery();

            List<Resource> ret = new ArrayList<Resource>();
            
            while(result.next())
            {
                ret.add(new Resource(this, result.getInt(1)));
            }
            
            result.close();
            
            return ret;
        } 
        catch (SQLException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return new ArrayList<Resource>();
        }
    }
    
    public List<Type> getTypes()
    {
        QueryBuilder builder = new QueryBuilder(database);
        
        ResultSet result = builder.select(Type.SQL_COL_ID)
                                    .from(Type.SQL_TABLE)
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
        
        ResultSet result = builder.select(Group.SQL_COL_ID)
                                    .from(Group.SQL_TABLE)
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
                .from(Resource.SQL_TABLE)
                .where(Resource.SQL_COL_ID, resourceId)
                .executeUpdate();

        if(affectedRows == 1)
            resourceRemoveEvents.forEach(event -> event.accept(resourceId));
        
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
                .from(Type.SQL_TABLE)
                .where(Type.SQL_COL_ID, typeId)
                .executeUpdate();

        if(affectedRows == 1)
            typeRemoveEvents.forEach(event -> event.accept(typeId));
        
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
                .from(Group.SQL_TABLE)
                .where(Group.SQL_COL_ID, groupId)
                .executeUpdate();

        if(affectedRows == 1)
            groupRemoveEvents.forEach(event -> event.accept(groupId));
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
    }
    
    public boolean removeGroup(Group group)
    {
        return removeGroup(group.getId());
    }

    public List<Consumer<Resource>> getResourceAddEvents()
    {
        return resourceAddEvents;
    }
    
    public List<Consumer<Resource>> getResourceEditEvents()
    {
        return resourceEditEvents;
    }
    
    public List<Consumer<Integer>> getResourceRemoveEvents()
    {
        return resourceRemoveEvents;
    }
    
    public List<Consumer<Type>> getTypeAddEvents()
    {
        return typeAddEvents;
    }
    
    public List<Consumer<Type>> getTypeEditEvents()
    {
        return typeEditEvents;
    }
    
    public List<Consumer<Integer>> getTypeRemoveEvents()
    {
        return typeRemoveEvents;
    }
    
    public List<Consumer<Group>> getGroupAddEvents()
    {
        return groupAddEvents;
    }
    
    public List<Consumer<Group>> getGroupEditEvents()
    {
        return groupEditEvents;
    }
    
    public List<Consumer<Integer>> getGroupRemoveEvents()
    {
        return groupRemoveEvents;
    }
    
    public List<BiConsumer<Group, Resource>> getResourceAddToGroupEvents()
    {
        return resourceAddToGroupEvents;
    }
    
    public List<BiConsumer<Group, Resource>> getResourceRemoveFromGroupEvents()
    {
        return resourceRemoveFromGroupEvents;
    }

    //default visibility on purpose; to emulate friends from C++; only Type should call this
    void fireResourceEditEvent(Resource resource)
    {
        resourceEditEvents.forEach(event -> event.accept(resource));
    }

    //default visibility on purpose; to emulate friends from C++; only Type should call this
    void fireTypeEditEvent(Type type)
    {
        typeEditEvents.forEach(event -> event.accept(type));
    }

    //default visibility on purpose; to emulate friends from C++; only Type should call this
    void fireGroupEditEvent(Group group)
    {
        groupEditEvents.forEach(event -> event.accept(group));
    }

    //default visibility on purpose; to emulate friends from C++; only Type should call this
    void fireResourceAddToGroupEvent(Group group, Resource resource)
    {
        resourceAddToGroupEvents.forEach(event -> event.accept(group, resource));
    }

    //default visibility on purpose; to emulate friends from C++; only Type should call this
    void fireResourceRemoveFromGroupEvent(Group group, Resource resource)
    {
        resourceRemoveFromGroupEvents.forEach(event -> event.accept(group, resource));
    }

    @Override
    public void close() throws IOException
    {
        database.close();
    }
}
