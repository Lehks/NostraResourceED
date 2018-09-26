package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Type;

/**
 * A filter that allows it to filter after the type name.
 * 
 * @author Lukas Reichmann
 */
public class TypeNameFilter extends Filter
{
    private final String name;

    public TypeNameFilter(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String generateSQLImpl()
    {
        return Type.SQL_TABLE + "." + Type.SQL_COL_NAME + " = " + quotedOrNull(name);
    }
}
