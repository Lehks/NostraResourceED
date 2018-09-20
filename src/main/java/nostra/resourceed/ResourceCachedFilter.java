package nostra.resourceed;

public class ResourceCachedFilter extends Filter
{
    private final String cached;

    public ResourceCachedFilter(final String cached)
    {
        this.cached = cached;
    }

    public String getCached()
    {
        return cached;
    }
    
    @Override
    public String generateSQLImpl()
    {
        return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " = '" + cached + "'";
    }
}
