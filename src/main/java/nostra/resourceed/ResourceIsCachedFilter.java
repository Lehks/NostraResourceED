package nostra.resourceed;

public class ResourceIsCachedFilter extends Filter
{
    @Override
    public String generateSQLImpl()
    {
        return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " <> NULL";
    }
}
