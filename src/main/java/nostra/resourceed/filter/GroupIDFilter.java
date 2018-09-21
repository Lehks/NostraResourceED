package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Group;

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
