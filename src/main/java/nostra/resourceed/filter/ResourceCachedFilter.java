package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Resource;

/**
 * A filter that allows it to filter after the (resource) cache path.
 * 
 * @author Lukas Reichmann
 */
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
        return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " = " + quotedOrNull(cached);
    }
}
