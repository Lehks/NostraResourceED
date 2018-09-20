package nostra.resourceed;

public class ResourcePathFilter extends Filter
{
    private final String path;

    public ResourcePathFilter(final String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }
    
    @Override
    public String generateSQLImpl()
    {
        return Resource.SQL_TABLE + "." + Resource.SQL_COL_PATH + " = '" + path + "'";
    }
}
