package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Resource;

/**
 * A filter that allows it to filter after the (resource) path extension.
 * 
 * @author Lukas Reichmann
 */
public class ResourcePathFileExtensionFilter extends Filter
{
    private final String extension;

    /**
     * Constructs a new instance.
     * 
     * @param extension The extension used by the filter. This is without a leading
     *                  dot. To match the path "Texture.png", the extension "png"
     *                  needs to be used.
     */
    public ResourcePathFileExtensionFilter(final String extension)
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
        if (extension != null)
            return Resource.SQL_TABLE + "." + Resource.SQL_COL_PATH + " LIKE '%." + extension + "'";
        else
            return "1 = 2"; // return false
    }
}
