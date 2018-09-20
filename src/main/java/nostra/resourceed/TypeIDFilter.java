package nostra.resourceed;

public class TypeIDFilter extends Filter
{
    private final int id;

    public TypeIDFilter(final int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
    
    @Override
    public String generateSQLImpl()
    {
        return Type.SQL_TABLE + "." + Type.SQL_COL_ID + " = " + id;
    }
}
