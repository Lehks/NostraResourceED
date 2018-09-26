package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Group;

/**
 * A filter that allows it to filter after the group ID.
 * 
 * @author Lukas Reichmann
 */
public class GroupIDFilter extends Filter
{
    private final int id;

    public GroupIDFilter(final int id)
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
        return Group.SQL_TABLE + "." + Group.SQL_COL_ID + " = " + id;
    }
}
