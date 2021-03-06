package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a row in the table "Groups" in a resource database.
 * 
 * This class allows it to edit the columns of that table (using getter and
 * setter methods).
 * 
 * @author Leslie Marxen, Lukas Reichmann
 */
public class Group
{
    /**
     * The name of the Groups table in SQL.
     */
    public static final String SQL_TABLE = "Groups";

    /**
     * The name of the ID column Groups table in SQL.
     */
    public static final String SQL_COL_ID = "ID";

    /**
     * The name of the name column Groups table in SQL.
     */
    public static final String SQL_COL_NAME = "name";

    /**
     * The SQL code to create the Groups table.
     */
    // @formatter:off
    public static final String SQL_CREATE_TABLE = 
            "CREATE TABLE IF NOT EXISTS " + SQL_TABLE + "(" +
                    SQL_COL_ID   + " INTEGER NOT NULL,    " +
                    SQL_COL_NAME + " TEXT NOT NULL UNIQUE," +
                    "PRIMARY KEY(" + SQL_COL_ID + "));";
    // @formatter:on

    /**
     * The editor that this group is in.
     */
    private Editor editor;

    /**
     * The ID of the group.
     */
    private final int id;

    /**
     * The constructor. Only used by the library itself and not by a user.
     * 
     * @param editor The editor of this type.
     * @param id     The ID of this type.
     */
    // package private to emulate C++ friends; this constructor should not be used
    // by a user
    Group(Editor editor, final int id)
    {
        this.editor = editor;
        this.id = id;
    }

    /**
     * Returns the ID of this group.
     * 
     * @return The ID of this group.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the name of this group.
     * 
     * @return The name of this group.
     */
    public String getName()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());

        ResultSet result = builder.select(SQL_COL_NAME).from(SQL_TABLE).where(SQL_COL_ID, getId())
                .executeQuery();

        try
        {
            boolean hasNext = result.next();

            String ret = null;

            if (hasNext)
                ret = result.getString(1);
            else // this case should never happen if the instance was constructed by
                 // Editor.getGroup()
                ret = null;

            result.close();

            return ret;
        }
        catch (SQLException e)
        {
            // should never happen
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the name of this group.
     * 
     * @param name The new name.
     * @return True, if the name was successfully set, false if not.
     */
    public boolean setName(String name)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());
        int affectedRows = builder.update(SQL_TABLE).set(SQL_COL_NAME, name).where(SQL_COL_ID, getId())
                .executeUpdate();

        if (affectedRows == 1)
            editor.fireGroupEditEvent(this);

        return affectedRows == 1; // can never be larger than 1, because selection is done through the primary
                                  // key
    }

    /**
     * Returns the editor that this type is in.
     * 
     * @return The editor.
     */
    public Editor getEditor()
    {
        return editor;
    }

    /**
     * Returns the members of this group.
     * 
     * @return The members.
     */
    public List<Resource> getMembers()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());

        ResultSet result = builder.select(Editor.GROUPS_RESOURCES_SQL_COL_RESOURCE_ID)
                .from(Editor.GROUPS_RESOURCES_SQL_TABLE)
                .where(Editor.GROUPS_RESOURCES_SQL_COL_GROUP_ID, getId()).executeQuery();

        try
        {
            List<Resource> ret = new ArrayList<Resource>();

            while (result.next())
            {
                ret.add(new Resource(editor, result.getInt(1)));
            }

            result.close();

            return ret;
        }
        catch (SQLException e)
        {
            // should never happen
            e.printStackTrace();
            return new ArrayList<Resource>();
        }
    }

    /**
     * Checks if a resource is a member of this group.
     * 
     * @param resourceId The resource to check.
     * @return True, if the resource is a member, false if not.
     */
    public boolean isMember(int resourceId)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());

        // TODO: optimize with AND (WHERE groupID = id AND resourceID = resourceID)
        ResultSet result = builder.select(Editor.GROUPS_RESOURCES_SQL_COL_RESOURCE_ID)
                .from(Editor.GROUPS_RESOURCES_SQL_TABLE)
                .where(Editor.GROUPS_RESOURCES_SQL_COL_GROUP_ID, getId()).executeQuery();

        try
        {
            /*
             * Any value in the result set does not matter, because: We only care about if
             * the type exists or not There is either one or none result, since we are
             * querying a primary key value
             * 
             * -> If there is a next, it the key was found; if not it was not found
             */
            // boolean hasNext = result.next();
            // result.close();

            // return hasNext;

            // the code above only works with the aforementioned optimization
            while (result.next())
            {
                if (result.getInt(1) == resourceId)
                    return true;
            }

            return false;
        }
        catch (SQLException e)
        {
            // should never happen
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a resource is a member of this group.
     * 
     * @param resource The resource to check.
     * @return True, if the resource is a member, false if not.
     */
    public boolean isMember(Resource resource)
    {
        return isMember(resource.getId());
    }

    /**
     * Removes a resource from a group.
     * 
     * @param resourceId The resource to remove.
     * @return True, if the resource was removed from the group.
     */
    public boolean removeMember(int resourceId)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());

        int affectedRows = builder.delete().from(Editor.GROUPS_RESOURCES_SQL_TABLE)
                .where(Editor.GROUPS_RESOURCES_SQL_COL_RESOURCE_ID + " = " + resourceId // bit of a hack to
                                                                                        // use AND
                        + " AND " + Editor.GROUPS_RESOURCES_SQL_COL_GROUP_ID, getId())
                .executeUpdate();

        if (affectedRows == 1)
            editor.fireResourceRemoveFromGroupEvent(this, editor.getResource(resourceId));

        return affectedRows == 1; // can never be larger than 1, because selection is done through the primary
                                  // key
    }

    /**
     * Removes a resource from a group.
     * 
     * @param resource The resource to remove.
     * @return True, if the resource was removed from the group.
     */
    public boolean removeMember(Resource resource)
    {
        return removeMember(resource.getId());
    }

    /**
     * Adds a resource to a group.
     * 
     * @param resourceID The resource to add.
     * @return True, if the resource was added to the group.
     */
    public boolean addMember(int resourceId)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());

        int affectedRows = builder.insert(Editor.GROUPS_RESOURCES_SQL_TABLE)
                .set(Editor.GROUPS_RESOURCES_SQL_COL_GROUP_ID, getId())
                .set(Editor.GROUPS_RESOURCES_SQL_COL_RESOURCE_ID, resourceId).executeUpdate();

        if (affectedRows == 1)
            editor.fireResourceAddToGroupEvent(this, editor.getResource(resourceId));

        return affectedRows == 1; // can never be larger than 1, because selection is done through the primary
                                  // key
    }

    /**
     * Adds a resource to a group.
     * 
     * @param resource The resource to add.
     * @return True, if the resource was added to the group.
     */
    public boolean addMember(Resource resource)
    {
        return addMember(resource.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj instanceof Group)
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
