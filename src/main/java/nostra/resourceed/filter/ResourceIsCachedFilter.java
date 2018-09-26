package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Resource;

/**
 * A filter that allows it to filter after the (resource) cache state (if the
 * resource is cached or not). A resource is not cached, if the cache path is
 * set to NULL in the database.
 * 
 * @author Lukas Reichmann
 */
public class ResourceIsCachedFilter extends Filter
{
    private boolean isCached;

    public ResourceIsCachedFilter(boolean isCached)
    {
        this.isCached = isCached;
    }

    @Override
    public String generateSQLImpl()
    {
        if (isCached)
            return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " <> NULL";
        else
            return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " = NULL";
    }
}
