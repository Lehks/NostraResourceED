package nostra.resourceed;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Editor implements Closeable
{
    /** Represents the Grouped Table name inside the database. */
    public static final String GROUPS_RESOURCES_SQL_TABLE = "GroupsResources";
    /** Represents the Grouped ResourceId column name inside the database. */
    public static final String GROUPS_RESOURCES_SQL_COL_RESOURCE_ID = "ResourcesID";
    /** Represents the Grouped GroupId column name inside the database. */
    public static final String GROUPS_RESOURCES_SQL_COL_GROUP_ID = "GroupsID";

    public static final String GROUPS_RESOURCES_SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + GROUPS_RESOURCES_SQL_TABLE + "` (" + 
            "   `" + GROUPS_RESOURCES_SQL_COL_RESOURCE_ID + "` INTEGER NOT NULL," + 
            "   `" + GROUPS_RESOURCES_SQL_COL_GROUP_ID    + "` INTEGER NOT NULL," + 
            "   PRIMARY KEY(`" + GROUPS_RESOURCES_SQL_COL_RESOURCE_ID + "`, `" + GROUPS_RESOURCES_SQL_COL_GROUP_ID + "`)," + 
            "   FOREIGN KEY(`" + GROUPS_RESOURCES_SQL_COL_RESOURCE_ID 
                + "`) REFERENCES `" + Resource.SQL_TABLE + "`(`" + Resource.SQL_COL_ID + "`) ON DELETE CASCADE ON UPDATE CASCADE," +
            "   FOREIGN KEY(`" + GROUPS_RESOURCES_SQL_COL_GROUP_ID 
                + "`) REFERENCES `" + Group.SQL_TABLE + "`(`" + Group.SQL_COL_ID + "`) ON DELETE CASCADE ON UPDATE CASCADE);";
    
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
    
    public static Editor newDatabase(File file) throws SQLException, 
                                                        AccessDeniedException, 
                                                        FileAlreadyExistsException, 
                                                        IOException
    {
        if(file.exists())
            throw new FileAlreadyExistsException(file.toString());
        
        if(!file.getParentFile().exists())
        {
            if(!file.getParentFile().mkdirs())
                throw new AccessDeniedException(file.toString());
        }
        
        file.createNewFile();
            
        Database database = new Database(file.getAbsolutePath());

        PreparedStatement stmt = database.prepQuery(Type.SQL_CREATE_TABLE);
        stmt.execute();

        stmt = database.prepQuery(Group.SQL_CREATE_TABLE);
        stmt.execute();
        
        stmt = database.prepQuery(Resource.SQL_CREATE_TABLE);
        stmt.execute();

        stmt = database.prepQuery(GROUPS_RESOURCES_SQL_CREATE_TABLE);
        stmt.execute();

        Editor editor = new Editor(database);
        
        return editor;
    }
    
    public Editor(String databasePath) throws SQLException
    {
        this(new Database(databasePath));
    }
    
    public Editor(File database) throws SQLException
    {
        this(database.getAbsolutePath());
    }
    
    public Editor(Database database) throws SQLException
    {
        this.database = database;
        
        checkDatabase();
        
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
    
    private void checkDatabase() throws SQLException
    {
        checkIfTableExists(Resource.SQL_TABLE);
        checkIfTableHasColumns(Resource.SQL_TABLE, Resource.SQL_COL_ID, 
                Resource.SQL_COL_PATH, Resource.SQL_COL_CACHED, Resource.SQL_COL_TYPE);
        
        checkIfTableExists(Type.SQL_TABLE);
        checkIfTableHasColumns(Type.SQL_TABLE, Type.SQL_COL_ID, Type.SQL_COL_NAME,Type.SQL_COL_DESCRIPTION);
        
        checkIfTableExists(Group.SQL_TABLE);
        checkIfTableHasColumns(Group.SQL_TABLE, Group.SQL_COL_ID, Group.SQL_COL_NAME);
        
        checkIfTableExists(GROUPS_RESOURCES_SQL_TABLE);
        checkIfTableHasColumns(GROUPS_RESOURCES_SQL_TABLE, GROUPS_RESOURCES_SQL_COL_GROUP_ID, 
                GROUPS_RESOURCES_SQL_COL_RESOURCE_ID);
    }
    
    private void checkIfTableExists(String table) throws SQLException
    {
        PreparedStatement stmt = database.prepQuery("SELECT * FROM sqlite_master WHERE type = 'table' AND name = ?");
        stmt.setString(1, table);
        
        ResultSet result = stmt.executeQuery();
        
        if(!result.next())
        {
            throw new SQLException("Database does not contain table \"" + table + "\"");
        }
    }
    
    private void checkIfTableHasColumns(String table, String... columns) throws SQLException
    {
        PreparedStatement stmt = database.prepQuery("PRAGMA table_info(" + table + ")");
        
        ResultSet result = stmt.executeQuery();
        
        List<String> resultColumns = new ArrayList<>();
        
        while(result.next())
        {
            resultColumns.add(result.getString(2));
        }
        
        //check if the sizes match and all requested columns are present
        if(!(resultColumns.size() == columns.length) && 
                resultColumns.containsAll(Arrays.asList(columns)))
        {
            throw new SQLException("The table \"" + table + "\" has a wrong schema.");
        }
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
    
    public Type addType(String name, String description)
    {
        QueryBuilder query = new QueryBuilder(database);
        
        int affectedRows = query.insert(Type.SQL_TABLE)
            .set(Type.SQL_COL_NAME, name)
            .set(Type.SQL_COL_DESCRIPTION, description)
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
            .append(Resource.SQL_TABLE)
            .append(".")
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
            .append(GROUPS_RESOURCES_SQL_TABLE)
            .append(" ON ")
            .append(Resource.SQL_TABLE)
            .append(".")
            .append(Resource.SQL_COL_ID)
            .append(" = ")
            .append(GROUPS_RESOURCES_SQL_TABLE)
            .append(".")
            .append(GROUPS_RESOURCES_SQL_COL_RESOURCE_ID);

        sql.append(" LEFT JOIN ")
            .append(Group.SQL_TABLE)
            .append(" ON ")
            .append(GROUPS_RESOURCES_SQL_TABLE)
            .append(".")
            .append(GROUPS_RESOURCES_SQL_COL_GROUP_ID)
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
