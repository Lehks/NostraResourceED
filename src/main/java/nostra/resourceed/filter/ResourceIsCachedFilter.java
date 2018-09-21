package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Resource;

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
        if(isCached)
            return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " <> NULL";
        else
            return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " = NULL";
    }
}
