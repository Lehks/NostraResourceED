package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Type;

/**
 * A filter that allows it to filter after the type ID.
 * 
 * @author Lukas Reichmann
 */
public class TypeIDFilter extends Filter
{
    private final int id;

    public TypeIDFilter(final int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    @Override
    public String generateSQLImpl()
    {
        return Type.SQL_TABLE + "." + Type.SQL_COL_ID + " = " + id;
    }
}
