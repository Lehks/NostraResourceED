package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Resource;

/**
 * A filter that allows it to filter after the resource ID.
 * 
 * @author Lukas Reichmann
 */
public class ResourceIDFilter extends Filter
{
    private final int id;

    public ResourceIDFilter(final int id)
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
        return Resource.SQL_TABLE + "." + Resource.SQL_COL_ID + " = " + id;
    }
}
