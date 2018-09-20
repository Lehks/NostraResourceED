package nostra.resourceed;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Type
{
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
        
        ResultSet result = builder.select(Editor.TYPE_NAME_COLUMN)
                                    .from(Editor.TYPE_TABLE)
                                    .where(Editor.TYPE_ID_COLUMN, getId())
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
        
        int affectedRows = builder.update(Editor.TYPE_TABLE)
                .set(Editor.TYPE_NAME_COLUMN, name)
                .where(Editor.TYPE_ID_COLUMN, getId())
                .executeUpdate();
        
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
        return "Type(id=" + getId() + ";name=" + getName() + ")";
    }
}
