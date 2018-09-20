package nostra.resourceed;

public class GroupNameFilter extends Filter
{
    private final String name;

    public GroupNameFilter(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    @Override
    public String generateSQLImpl()
    {
        return Group.SQL_TABLE + "." + Group.SQL_COL_NAME + " = '" + name + "'";
    }
}
