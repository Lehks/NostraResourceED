package nostra.resourceed.filter;

import nostra.resourceed.Filter;
import nostra.resourceed.Type;

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
        return Type.SQL_TABLE + "." + Type.SQL_COL_NAME + " = " +  quotedOrNull(name);
    }
}
