package nostra.resourceed;

import java.util.ArrayList;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class QueryBuilder
{
    private static final short QUERY_TYPE_INSERT = 8;
    private static final short QUERY_TYPE_SELECT = 4;
    private static final short QUERY_TYPE_UPDATE = 2;
    private static final short QUERY_TYPE_DELETE = 1;

    private static final short QUERY_VALUE_DATA_TYPE_STRING  = 1;
    private static final short QUERY_VALUE_DATA_TYPE_INTEGER = 2;
    private static final short QUERY_VALUE_DATA_TYPE_DOUBLE  = 4;

    private Database db;
    private short type;
    private int limit;
    private int offset;
    private int insertId;

    private ArrayList<String> affectedColumns;
    private ArrayList<String> affectedTables;
    private ArrayList<String> affectedJoins;

    private ArrayList<String>  filterColumns;
    private ArrayList<Short>   filterDatatypes;
    private ArrayList<String>  filterStringValues;
    private ArrayList<Integer> filterIntegerValues;
    private ArrayList<Double>  filterDoubleValues;

    private ArrayList<Short>   affectedColumnDatatypes;
    private ArrayList<String>  affectedColumnStringValues;
    private ArrayList<Integer> affectedColumnIntegerValues;
    private ArrayList<Double>  affectedColumnDoubleValues;

    private ArrayList<String> groupColumns;
    private ArrayList<String> orderColumns;
    private ArrayList<String> havingColumns;


    public QueryBuilder(Database db)
    {
        this.db = db;

        this.affectedColumns = new ArrayList<String>();
        this.affectedTables  = new ArrayList<String>();
        this.affectedJoins   = new ArrayList<String>();

        this.filterColumns       = new ArrayList<String>();
        this.filterDatatypes     = new ArrayList<Short>();
        this.filterStringValues  = new ArrayList<String>();
        this.filterIntegerValues = new ArrayList<Integer>();
        this.filterDoubleValues  = new ArrayList<Double>();

        this.affectedColumnDatatypes     = new ArrayList<Short>();
        this.affectedColumnStringValues  = new ArrayList<String>();
        this.affectedColumnIntegerValues = new ArrayList<Integer>();
        this.affectedColumnDoubleValues  = new ArrayList<Double>();

        this.groupColumns  = new ArrayList<String>();
        this.orderColumns  = new ArrayList<String>();
        this.havingColumns = new ArrayList<String>();


        this.clear();
    }


    public QueryBuilder clear()
    {
        this.type     = 0;
        this.limit    = 0;
        this.offset   = 0;
        this.insertId = 0;

        this.affectedColumns.clear();
        this.affectedTables.clear();
        this.affectedJoins.clear();

        this.filterColumns.clear();
        this.filterDatatypes.clear();
        this.filterStringValues.clear();
        this.filterIntegerValues.clear();
        this.filterDoubleValues.clear();

        this.affectedColumnDatatypes.clear();
        this.affectedColumnStringValues.clear();
        this.affectedColumnIntegerValues.clear();
        this.affectedColumnDoubleValues.clear();

        this.groupColumns.clear();
        this.orderColumns.clear();
        this.havingColumns.clear();

        return this;
    }


    public QueryBuilder select(String col)
    {
        this.type = QUERY_TYPE_SELECT;

        this.affectedColumns.add(col);

        return this;
    }
    public QueryBuilder select(String[] cols)
    {
        for (String col : cols) this.select(col);

        return this;
    }


    public QueryBuilder from(String table)
    {
        this.affectedTables.add(table);

        return this;
    }


    public QueryBuilder where(String column, String value)
    {
        if (column.trim().length() == 0) return this;
        if (!column.contains("?")) column = column.trim() + " ?";

        this.filterColumns.add(column.trim());
        this.filterStringValues.add(value);
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }
    public QueryBuilder where(String column, Integer value)
    {
        if (column.trim().length() == 0) return this;
        if (!column.contains("?")) column = column + " ?";

        this.filterColumns.add(column.trim());
        this.filterIntegerValues.add(value);
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_INTEGER);

        return this;
    }
    public QueryBuilder where(String column, Double value)
    {
        if (column.trim().length() == 0) return this;
        if (!column.contains("?")) column = column + " ?";

        this.filterColumns.add(column.trim());
        this.filterDoubleValues.add(value);
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_DOUBLE);

        return this;
    }
    public QueryBuilder where(String column)
    {
        if (column.trim().length() == 0) return this;
        if (!column.contains("?")) column += " ?";

        this.filterColumns.add(column.trim());
        this.filterStringValues.add("");
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }


    public QueryBuilder innerJoin(String table)
    {
        this.affectedJoins.add("INNER JOIN " + table);

        return this;
    }


    public QueryBuilder leftJoin(String table)
    {
        this.affectedJoins.add("LEFT JOIN " + table);

        return this;
    }


    public QueryBuilder naturalJoin(String table)
    {
        this.affectedJoins.add("NATURAL JOIN " + table);

        return this;
    }


    public QueryBuilder rightJoin(String table)
    {
        this.affectedJoins.add("RIGHT JOIN " + table);

        return this;
    }


    public QueryBuilder groupBy(String column)
    {
        this.groupColumns.add(column);

        return this;
    }
    public QueryBuilder groupBy(String[] columns)
    {
        for (String col : columns) this.groupBy(col);

        return this;
    }


    public QueryBuilder orderBy(String column)
    {
        this.orderColumns.add(column);

        return this;
    }
    public QueryBuilder orderBy(String[] columns)
    {
        for (String col : columns) this.orderBy(col);

        return this;
    }


    public QueryBuilder having(String columnValue)
    {
        this.havingColumns.add(columnValue);

        return this;
    }


    public QueryBuilder update(String table)
    {
        this.type = QUERY_TYPE_UPDATE;

        this.affectedTables.clear();
        this.affectedTables.add(table);

        return this;
    }


    public QueryBuilder set(String column, String value)
    {
        if (column.trim().length() == 0) return this;

        if (this.type == QUERY_TYPE_INSERT) {
            this.affectedColumns.add(column);
        }
        else {
            this.affectedColumns.add(column + " = ?");
        }
        this.affectedColumnStringValues.add(value);
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }
    public QueryBuilder set(String column, int value)
    {
        if (column.trim().length() == 0) return this;

        if (this.type == QUERY_TYPE_INSERT) {
            this.affectedColumns.add(column);
        }
        else {
            this.affectedColumns.add(column + " = ?");
        }
        this.affectedColumnIntegerValues.add(value);
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_INTEGER);

        return this;
    }
    public QueryBuilder set(String column, double value)
    {
        if (column.trim().length() == 0) return this;

        if (this.type == QUERY_TYPE_INSERT) {
            this.affectedColumns.add(column);
        }
        else {
            this.affectedColumns.add(column + " = ?");
        }
        this.affectedColumnDoubleValues.add(value);
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_DOUBLE);

        return this;
    }
    public QueryBuilder set(String column)
    {
        if (column.trim().length() == 0) return this;

        this.affectedColumns.add(column + "?");
        this.affectedColumnStringValues.add("");
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }


    public QueryBuilder delete()
    {
        this.type = QUERY_TYPE_DELETE;
        return this;
    }

    public QueryBuilder insert(String table)
    {
        this.type = QUERY_TYPE_INSERT;

        this.affectedTables.clear();
        this.affectedTables.add(table);

        return this;
    }


    public QueryBuilder limit(int num)
    {
        this.limit = Math.abs(num);

        return this;
    }


    public QueryBuilder offset(int num)
    {
        this.offset = Math.abs(num);

        return this;
    }


    private PreparedStatement selectStatement()
    {
        // Return nothing if we have no cols or tables selected
        if (this.affectedColumns.size() == 0 || this.affectedTables.size() == 0) {
            return this.db.prepQuery("");
        }

        StringBuilder q = new StringBuilder("SELECT ");

        // Select columns
        q.append(String.join(", ", this.affectedColumns));
        q.append(" \n");

        // From tables
        q.append("FROM ");
        q.append(String.join(", ", this.affectedTables));
        q.append(" \n");

        // Join tables
        if (this.affectedJoins.size() > 0) {
            q.append(String.join(" \n", this.affectedJoins));
            q.append(" \n");
        }

        // Filter
        if (this.filterColumns.size() > 0) {
            q.append("WHERE ");
            q.append(String.join(" AND ", this.filterColumns));
            q.append(" \n");
        }

        // Group By
        if (this.groupColumns.size() > 0) {
            q.append("GROUP BY ");
            q.append(String.join(", ", this.groupColumns));
            q.append(" \n");

            // Having
            if (this.havingColumns.size() > 0) {
                q.append("HAVING ");
                q.append(String.join(" AND ", this.havingColumns));
                q.append(" \n");
            }
        }

        // Order by
        if (this.orderColumns.size() > 0) {
            q.append("ORDER BY ");
            q.append(String.join(", ", this.orderColumns));
            q.append(" \n");
        }

        // Limit
        if (this.limit > 0) {
            q.append("LIMIT ").append(this.limit);
            q.append(" \n");
        }

        // Offset
        if (this.offset != 0) {
            q.append("OFFSET ").append(this.offset);
            q.append(" \n");
        }

        return this.sqlToStatement(q.toString());
    }


    private PreparedStatement updateStatement()
    {
        if (this.affectedTables.size() == 0 || this.affectedColumns.size() == 0) {
            return this.db.prepQuery("");
        }

        StringBuilder q = new StringBuilder("UPDATE ");

        // Table
        q.append(this.affectedTables.get(0));
        q.append(" \n");

        // Set column values
        q.append("SET ");
        q.append(String.join(", \n", this.affectedColumns));
        q.append(" \n");

        // Filter
        if (this.filterColumns.size() > 0) {
            q.append("WHERE ");
            q.append(String.join(" AND ", this.filterColumns));
            q.append(" \n");
        }

        // Limit
        if (this.limit > 0) {
            q.append("LIMIT ").append(this.limit);
            q.append(" \n");
        }

        // Offset
        if (this.offset != 0) {
            q.append("OFFSET ").append(this.offset);
            q.append(" \n");
        }

        return this.sqlToStatement(q.toString());
    }


    private PreparedStatement deleteStatement()
    {
        if (this.affectedTables.size() == 0) {
            return this.db.prepQuery("");
        }

        StringBuilder q = new StringBuilder("DELETE FROM ");

        // Table
        q.append(this.affectedTables.get(0));
        q.append(" \n");

        // Filter
        if (this.filterColumns.size() > 0) {
            q.append("WHERE ");
            q.append(String.join(" AND ", this.filterColumns));
            q.append(" \n");
        }

        // Limit
        if (this.limit > 0) {
            q.append("LIMIT ").append(this.limit);
            q.append(" \n");
        }

        // Offset
        if (this.offset != 0) {
            q.append("OFFSET ").append(this.offset);
            q.append(" \n");
        }

        return this.sqlToStatement(q.toString());
    }


    private PreparedStatement insertStatement()
    {
        if (this.affectedTables.size() == 0 || this.affectedColumns.size() == 0) {
            return this.db.prepQuery("");
        }

        StringBuilder q = new StringBuilder("INSERT INTO ");

        // Table
        q.append(this.affectedTables.get(0));
        q.append(" \n");

        // Columns
        q.append(" (");
        q.append(String.join(", ", this.affectedColumns));
        q.append(") \n");

        // Values
        q.append("VALUES (");
        for (int i = 0; i < this.affectedColumns.size(); i++)
        {
            q.append("?");

            if (i+1 < this.affectedColumns.size()) {
                q.append(", ");
            }
        }
        q.append(") \n");

        return this.sqlToStatement(q.toString());
    }


    private PreparedStatement sqlToStatement(String sql)
    {
        int n = 0;
        int i = 0;
        int x = 1;
        int istr = 0;
        int iint = 0;
        int idbl = 0;

        // Create prepared statement
        PreparedStatement s;

        if (this.type == QUERY_TYPE_INSERT) {
            s = this.db.prepQuery(sql, Statement.RETURN_GENERATED_KEYS);
        }
        else {
            s = this.db.prepQuery(sql);
        }

        // First, fill in placeholders for insert and update queries
        if (this.affectedColumnDatatypes.size() > 0) {
            n = this.affectedColumnDatatypes.size();

            try {
                for (i = 0; i < n; i++)
                {
                    switch (this.affectedColumnDatatypes.get(i))
                    {
                        case QUERY_VALUE_DATA_TYPE_STRING:
                            s.setString(x, this.affectedColumnStringValues.get(istr));

                            istr++;
                            break;

                        case QUERY_VALUE_DATA_TYPE_INTEGER:
                            s.setInt(x, this.affectedColumnIntegerValues.get(iint));
                            iint++;
                            break;

                        case QUERY_VALUE_DATA_TYPE_DOUBLE:
                            s.setDouble(x, this.affectedColumnDoubleValues.get(idbl));
                            idbl++;
                            break;
                    }

                    x++;
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        // Continue replacing placeholders of query filters
        n = this.filterColumns.size();
        i = 0;
        istr = 0;
        iint = 0;
        idbl = 0;

        if (n > 0) {
            try {
                for (i = 0; i < n; i++)
                {
                    switch (this.filterDatatypes.get(i))
                    {
                        case QUERY_VALUE_DATA_TYPE_STRING:
                            s.setString(x, this.filterStringValues.get(istr));
                            istr++;
                            break;

                        case QUERY_VALUE_DATA_TYPE_INTEGER:
                            s.setInt(x, this.filterIntegerValues.get(iint));
                            iint++;
                            break;

                        case QUERY_VALUE_DATA_TYPE_DOUBLE:
                            s.setDouble(x, this.filterDoubleValues.get(idbl));
                            idbl++;
                            break;
                    }

                    x++;
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        // Lastly, do the same with having.


        return s;
    }


    private void setInsertId(PreparedStatement statement)
    {
        try {
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                this.insertId = generatedKeys.getInt(1);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Could not get last inserted ID: " + e.getMessage());
        }
    }


    public int getInsertId()
    {
        return this.insertId;
    }


    public PreparedStatement toStatement()
    {
        switch (this.type)
        {
            case QUERY_TYPE_SELECT:
                return this.selectStatement();

            case QUERY_TYPE_UPDATE:
                return this.updateStatement();

            case QUERY_TYPE_DELETE:
                return this.deleteStatement();

            case QUERY_TYPE_INSERT:
                return this.insertStatement();

            default:
                return this.db.prepQuery("");
        }
    }


    public int executeUpdate()
    {
        int numAffected = 0;
        PreparedStatement statement;
        try
        {
            statement   = this.toStatement();
            numAffected = statement.executeUpdate();

            if (this.type == QUERY_TYPE_INSERT) {
                this.setInsertId(statement);
            }
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
        }

        return numAffected;
    }


    public ResultSet executeQuery()
    {
        ResultSet rs = null;
        PreparedStatement statement;

        try
        {
            statement = this.toStatement();
            rs = this.toStatement().executeQuery();

            if (this.type == QUERY_TYPE_INSERT) {
                this.setInsertId(statement);
            }
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
        }

        return rs;
    }
}

