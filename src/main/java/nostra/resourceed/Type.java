package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Type
{
    /** Represents the Type table name inside the database. */
    public static final String SQL_TABLE = "resourceType";
    /** Represents the Type Id column name inside the database. */
    public static final String SQL_COL_ID = "tid";
    /** Represents the Type Name column name inside the database */
    public static final String SQL_COL_NAME = "name";

    private Editor editor;
    
    private final int id;
    
    public Type(Editor editor, final int id)
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
            else //this case should never happen if the instance was constructed by Editor.getType()
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
            editor.fireTypeEditEvent(this);
        
        return affectedRows == 1; //can never be larger than 1, because selection is done through the primary key
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
        
        if(obj instanceof Type)
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
