package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class that represents a row in the table "Types" in a resource database.
 * 
 * This class allows it to edit the columns of that table (using getter and
 * setter methods).
 * 
 * @author Leslie Marxen, Lukas Reichmann
 */
public class Type
{
    /**
     * The name of the Types table in SQL.
     */
    public static final String SQL_TABLE = "Types";

    /**
     * The name of the ID column Types table in SQL.
     */
    public static final String SQL_COL_ID = "ID";

    /**
     * The name of the name column Types table in SQL.
     */
    public static final String SQL_COL_NAME = "name";

    /**
     * The name of the description column Types table in SQL.
     */
    public static final String SQL_COL_DESCRIPTION = "description";

    /**
     * The SQL code to create the Types table.
     */
    // @formatter:off
    public static final String SQL_CREATE_TABLE = 
            "CREATE TABLE IF NOT EXISTS " + SQL_TABLE + "(       " +
                   SQL_COL_ID          + " INTEGER NOT NULL,    " +
                   SQL_COL_NAME        + " TEXT NOT NULL UNIQUE," +
                   SQL_COL_DESCRIPTION + " TEXT,                " +
                   "PRIMARY KEY(" + SQL_COL_ID + "));";
    // @formatter:on

    /**
     * The editor that this type is in.
     */
    private Editor editor;

    /**
     * The ID of the type.
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
    Type(Editor editor, final int id)
    {
        this.editor = editor;
        this.id = id;
    }

    /**
     * Returns the ID of this type.
     * 
     * @return The ID of this type.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the name of this type.
     * 
     * @return The name of this type.
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
                 // Editor.getType()
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
     * Sets the name of this type.
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
            editor.fireTypeEditEvent(this);

        return affectedRows == 1; // can never be larger than 1, because selection is done through the primary
                                  // key
    }

    /**
     * Returns the description of this type.
     * 
     * @return The description of this type.
     */
    public String getDescription()
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());

        ResultSet result = builder.select(SQL_COL_DESCRIPTION).from(SQL_TABLE).where(SQL_COL_ID, getId())
                .executeQuery();

        try
        {
            boolean hasNext = result.next();

            String ret = null;

            if (hasNext)
                ret = result.getString(1);
            else // this case should never happen if the instance was constructed by
                 // Editor.getType()
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
     * Sets the description of this type.
     * 
     * @param description The new description.
     * @return True, if the description was successfully set, false if not.
     */
    public boolean setDescription(String description)
    {
        QueryBuilder builder = new QueryBuilder(editor.getDatabase());

        int affectedRows = builder.update(SQL_TABLE).set(SQL_COL_DESCRIPTION, description)
                .where(SQL_COL_ID, getId()).executeUpdate();

        if (affectedRows == 1)
            editor.fireTypeEditEvent(this);

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

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj instanceof Type)
        {
            Type type = (Type) obj;

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
