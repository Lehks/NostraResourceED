package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Resource;

public class ResourceCachedFileExtensionFilter extends Filter
{
    private final String extension;

    public ResourceCachedFileExtensionFilter(final String extension)
    {
        this.extension = extension;
    }

    public String getExtension()
    {
        return extension;
    }
    
    @Override
    public String generateSQLImpl()
    {
        if(extension != null)
            return Resource.SQL_TABLE + "." + Resource.SQL_COL_CACHED + " LIKE '%." + extension + "'";
        else
            return "1 = 2"; //return false
    }
}
