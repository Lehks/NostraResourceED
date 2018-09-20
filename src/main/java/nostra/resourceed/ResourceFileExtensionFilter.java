package nostra.resourceed;

public class ResourceFileExtensionFilter extends Filter
{
    private final String extension;

    public ResourceFileExtensionFilter(final String extension)
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
        return Resource.SQL_TABLE + "." + Resource.SQL_COL_PATH + " LIKE '%." + extension + "'";
    }
}
