package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Resource;

/**
 * A filter that allows it to filter after the (resource) path.
 * 
 * @author Lukas Reichmann
 */
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
        return isEquals(Resource.SQL_TABLE + "." + Resource.SQL_COL_PATH, path);
    }
}
