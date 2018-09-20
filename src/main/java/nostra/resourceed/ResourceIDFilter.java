package nostra.resourceed;

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
