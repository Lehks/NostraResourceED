package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Type;

/**
 * A filter that allows it to filter after the type description.
 * 
 * @author Lukas Reichmann
 */
public class TypeDescriptionFilter extends Filter
{
    private final String description;

    public TypeDescriptionFilter(final String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String generateSQLImpl()
    {
        return Type.SQL_TABLE + "." + Type.SQL_COL_DESCRIPTION + " = " + quotedOrNull(description);
    }
}
