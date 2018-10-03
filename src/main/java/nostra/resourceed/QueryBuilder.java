package nostra.resourceed;

import java.util.ArrayList;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * A class that can build SQL queries.
 * 
 * @author Tobias Kuhn
 */
public class QueryBuilder
{
    /**
     * Query type id's. Used internally.
     */
    private static final short QUERY_TYPE_INSERT = 8;
    private static final short QUERY_TYPE_SELECT = 4;
    private static final short QUERY_TYPE_UPDATE = 2;
    private static final short QUERY_TYPE_DELETE = 1;

    /**
     * Statement value data types. Used internally.
     */
    private static final short QUERY_VALUE_DATA_TYPE_STRING = 1;
    private static final short QUERY_VALUE_DATA_TYPE_INTEGER = 2;
    private static final short QUERY_VALUE_DATA_TYPE_DOUBLE = 4;

    /**
     * Instance of Database.
     */
    private Database db;
    
    /**
     * Current query type.
     */
    private short type;
    
    /**
     * Query limit clause
     */
    private int limit;

    /**
     * Query offset clause
     */
    private int offset;

    /**
     * Contains the primary key for a new row (after insert query has run)
     */
    private int insertId;

    /**
     * Arrays holding the selected tables and columns by the user.
     */
    private ArrayList<String> affectedColumns;
    private ArrayList<String> affectedTables;
    private ArrayList<String> affectedJoins;

    /**
     * Arrays holding the filtered columns and values (WHERE... AND)
     */
    private ArrayList<String> filterColumns;
    private ArrayList<Short> filterDatatypes;
    private ArrayList<String> filterStringValues;
    private ArrayList<Integer> filterIntegerValues;
    private ArrayList<Double> filterDoubleValues;

    /**
     * Mainly used for UPDATE type of queries. (SET x = y)
     */
    private ArrayList<Short> affectedColumnDatatypes;
    private ArrayList<String> affectedColumnStringValues;
    private ArrayList<Integer> affectedColumnIntegerValues;
    private ArrayList<Double> affectedColumnDoubleValues;

    /**
     * Holds the group, order and having clauses
     */
    private ArrayList<String> groupColumns;
    private ArrayList<String> orderColumns;
    private ArrayList<String> havingColumns;

    public QueryBuilder(Database db)
    {
        this.db = db;

        this.affectedColumns = new ArrayList<String>();
        this.affectedTables = new ArrayList<String>();
        this.affectedJoins = new ArrayList<String>();

        this.filterColumns = new ArrayList<String>();
        this.filterDatatypes = new ArrayList<Short>();
        this.filterStringValues = new ArrayList<String>();
        this.filterIntegerValues = new ArrayList<Integer>();
        this.filterDoubleValues = new ArrayList<Double>();

        this.affectedColumnDatatypes = new ArrayList<Short>();
        this.affectedColumnStringValues = new ArrayList<String>();
        this.affectedColumnIntegerValues = new ArrayList<Integer>();
        this.affectedColumnDoubleValues = new ArrayList<Double>();

        this.groupColumns = new ArrayList<String>();
        this.orderColumns = new ArrayList<String>();
        this.havingColumns = new ArrayList<String>();

        this.clear();
    }

    /**
     * Clears all query data.
     * This method should be called after a query has been executed,
     * so you can re-use this instance.
     *
     * @return QueryBuilder
     */
    public QueryBuilder clear()
    {
        this.type = 0;
        this.limit = 0;
        this.offset = 0;
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

    /**
     * Sets the query type to SELECT, and selects the given columns.
     * Example:
     * qb.select("colA").select("colB, colC").select("colD")...
     *
     * @param col The name of the column(s) to select.
     * @return QueryBuilder
     */
    public QueryBuilder select(String col)
    {
        this.type = QUERY_TYPE_SELECT;

        this.affectedColumns.add(col);

        return this;
    }

    /**
     * Sets the query type to SELECT, and selects the given columns.
     * Example:
     * String[] str = {"tblA", "tblB", "tblC"};
     * qb.select(str)...
     *
     * @param cols The array of names to select.
     * @return QueryBuilder
     */
    public QueryBuilder select(String[] cols)
    {
        for (String col : cols)
            this.select(col);

        return this;
    }

    /**
     * Selects one or more tables. Used in SELECT and DELETE queries.
     * Example:
     * qb.select("colA").from("tableA")
     *
     * @param table Table name to select
     * @return QueryBuilder
     */
    public QueryBuilder from(String table)
    {
        this.affectedTables.add(table);

        return this;
    }

    /**
     * Filter the query by the given column-value pair.
     * Example:
     * qb.select("colA").from("tableA")
     * .where("colB > ", 20)
     * .where("colC = ", "test")
     *
     * @param column The name and operator of the column to filter
     * @param value The value of the column. Accepts String, int and Double
     * @return QueryBuilder
     */
    public QueryBuilder where(String column, String value)
    {
        if (column.trim().length() == 0)
            return this;
        if (!column.contains("?"))
            column = column.trim() + " ?";

        this.filterColumns.add(column.trim());
        this.filterStringValues.add(value);
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }


    /**
     * Filter the query by the given column-value pair.
     * Example:
     * qb.select("colA").from("tableA")
     * .where("colB > ", 20)
     * .where("colC = ", "test")
     *
     * @param column The name and operator of the column to filter
     * @param value The value of the column. Accepts String, int and Double
     * @return QueryBuilder
     */
    public QueryBuilder where(String column, Integer value)
    {
        if (column.trim().length() == 0)
            return this;
        if (!column.contains("?"))
            column = column + " = ?";

        this.filterColumns.add(column.trim());
        this.filterIntegerValues.add(value);
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_INTEGER);

        return this;
    }

    /**
     * Filter the query by the given column-value pair.
     * Example:
     * qb.select("colA").from("tableA")
     * .where("colB > ", 20)
     * .where("colC = ", "test")
     *
     * @param column The name and operator of the column to filter
     * @param value The value of the column. Accepts String, int and Double
     * @return QueryBuilder
     */
    public QueryBuilder where(String column, Double value)
    {
        if (column.trim().length() == 0)
            return this;
        if (!column.contains("?"))
            column = column + " = ?";

        this.filterColumns.add(column.trim());
        this.filterDoubleValues.add(value);
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_DOUBLE);

        return this;
    }

    /**
     * Filter the query by the given expression.
     * Example:
     * qb.select("colA").from("tableA")
     * .where("(fldA = 1 OR fldB = 2)")
     * .where("colC = ", "test")
     *
     * @param column The expression to use as filter
     * @return QueryBuilder
     */
    public QueryBuilder where(String column)
    {
        if (column.trim().length() == 0)
            return this;
        if (!column.contains("?"))
            column += " = ?";

        this.filterColumns.add(column.trim());
        this.filterStringValues.add("");
        this.filterDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }

    /**
     * Adds an inner join to the query.
     * Example:
     * qb.select("colA").from("tableA")
     * .innerJoin("tableB ON x = y")
     *
     * @param table The join statement.
     * @return QueryBuilder
     */
    public QueryBuilder innerJoin(String table)
    {
        this.affectedJoins.add("INNER JOIN " + table);

        return this;
    }

    /**
     * Adds a left join the query.
     * Example:
     * qb.select("colA").from("tableA")
     * .leftJoin("tableB ON x = y")
     *
     * @param table The join statement.
     * @return QueryBuilder
     */
    public QueryBuilder leftJoin(String table)
    {
        this.affectedJoins.add("LEFT JOIN " + table);

        return this;
    }

    /**
     * Adds a natural join to the query.
     * Example:
     * qb.select("colA").from("tableA")
     * .naturalJoin("tableB ON x = y")
     *
     * @param table The join statement.
     * @return QueryBuilder
     */
    public QueryBuilder naturalJoin(String table)
    {
        this.affectedJoins.add("NATURAL JOIN " + table);

        return this;
    }

    /**
     * Adds a right join to the query.
     * Example:
     * qb.select("colA").from("tableA")
     * .rightJoin("tableB ON x = y")
     *
     * @param table The join statement.
     * @return QueryBuilder
     */
    public QueryBuilder rightJoin(String table)
    {
        this.affectedJoins.add("RIGHT JOIN " + table);

        return this;
    }

    /**
     * Adds a GROUP BY to aggregate queries.
     * Example:
     * qb.select("colA").from("tableA")
     * .groupBy("fieldA")
     *
     * @param column The column to group by.
     * @return QueryBuilder
     */
    public QueryBuilder groupBy(String column)
    {
        this.groupColumns.add(column);

        return this;
    }

    /**
     * Adds a GROUP BY to aggregate queries.
     * Example:
     * qb.select("colA").from("tableA")
     * .groupBy("fieldA")
     *
     * @param columns The array of columns to group by.
     * @return QueryBuilder
     */
    public QueryBuilder groupBy(String[] columns)
    {
        for (String col : columns)
            this.groupBy(col);

        return this;
    }

    /**
     * Adds a ORDER BY statement to the query.
     * Example:
     * qb.select("colA").from("tableA")
     * .orderBy("fieldA ASC")
     *
     * @param column The column to order by.
     * @return QueryBuilder
     */
    public QueryBuilder orderBy(String column)
    {
        this.orderColumns.add(column);

        return this;
    }

    /**
     * Adds a ORDER BY statement to the query.
     * Example:
     * qb.select("colA").from("tableA")
     * .orderBy("fieldA ASC")
     *
     * @param columns The array of columns to order by.
     * @return QueryBuilder
     */
    public QueryBuilder orderBy(String[] columns)
    {
        for (String col : columns)
            this.orderBy(col);

        return this;
    }

    /**
     * Adds a GROUP BY to aggregate queries.
     * Example:
     * qb.select("colA").from("tableA")
     * .groupBy("fieldA")
     *
     * @param columnValue The column to group by.
     * @return QueryBuilder
     */
    public QueryBuilder having(String columnValue)
    {
        this.havingColumns.add(columnValue);

        return this;
    }

    /**
     * Sets the type of the query to UPDATE
     * Example:
     * qb.update("colA") ...
     *
     * @param table The table to update.
     * @return QueryBuilder
     */
    public QueryBuilder update(String table)
    {
        this.type = QUERY_TYPE_UPDATE;

        this.affectedTables.clear();
        this.affectedTables.add(table);

        return this;
    }

    /**
     * Updates a specific field in an UPDATE query
     * Example:
     * qb.update("tableA")set("colA", "valA") ...
     *
     * @param column The field to update.
     * @param value  The value to set.
     * @return QueryBuilder
     */
    public QueryBuilder set(String column, String value)
    {
        if (column.trim().length() == 0)
            return this;

        if (this.type == QUERY_TYPE_INSERT)
        {
            this.affectedColumns.add(column);
        }
        else
        {
            this.affectedColumns.add(column + " = ?");
        }
        this.affectedColumnStringValues.add(value);
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }

    /**
     * Updates a specific field in an UPDATE query
     * Example:
     * qb.update("tableA")set("colA", "valA") ...
     *
     * @param column The field to update.
     * @param value  The value to set.
     * @return QueryBuilder
     */
    public QueryBuilder set(String column, int value)
    {
        if (column.trim().length() == 0)
            return this;

        if (this.type == QUERY_TYPE_INSERT)
        {
            this.affectedColumns.add(column);
        }
        else
        {
            this.affectedColumns.add(column + " = ?");
        }
        this.affectedColumnIntegerValues.add(value);
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_INTEGER);

        return this;
    }

    /**
     * Updates a specific field in an UPDATE query
     * Example:
     * qb.update("tableA")set("colA", "valA") ...
     *
     * @param column The field to update.
     * @param value  The value to set.
     * @return QueryBuilder
     */
    public QueryBuilder set(String column, double value)
    {
        if (column.trim().length() == 0)
            return this;

        if (this.type == QUERY_TYPE_INSERT)
        {
            this.affectedColumns.add(column);
        }
        else
        {
            this.affectedColumns.add(column + " = ?");
        }
        this.affectedColumnDoubleValues.add(value);
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_DOUBLE);

        return this;
    }

    public QueryBuilder set(String column)
    {
        if (column.trim().length() == 0)
            return this;

        this.affectedColumns.add(column + "?");
        this.affectedColumnStringValues.add("");
        this.affectedColumnDatatypes.add(QUERY_VALUE_DATA_TYPE_STRING);

        return this;
    }

    /**
     * Sets the query type to DELETE
     * Example:
     * qb.delete().from("tableA") ...
     *
     * @return QueryBuilder
     */
    public QueryBuilder delete()
    {
        this.type = QUERY_TYPE_DELETE;
        return this;
    }


    /**
     * Sets the type of the query to INSERT
     * Example:
     * qb.insert("tableA") ...
     *
     * @param table The table to update.
     * @return QueryBuilder
     */
    public QueryBuilder insert(String table)
    {
        this.type = QUERY_TYPE_INSERT;

        this.affectedTables.clear();
        this.affectedTables.add(table);

        return this;
    }


    /**
     * Sets the limit of the query. Only relevant in
     * SELECT and DELETE queries.
     * Example:
     * qb.select("tableA").limit(10) ...
     *
     * @param num The number of rows to return
     * @return QueryBuilder
     */
    public QueryBuilder limit(int num)
    {
        this.limit = Math.abs(num);

        return this;
    }

    /**
     * Sets the offset for SELECT and DELETE queries.
     * Example:
     * qb.offset("colA") ...
     *
     * @param num The desired offset.
     * @return QueryBuilder
     */
    public QueryBuilder offset(int num)
    {
        this.offset = Math.abs(num);

        return this;
    }

    private PreparedStatement selectStatement()
    {
        // Return nothing if we have no cols or tables selected
        if (this.affectedColumns.size() == 0 || this.affectedTables.size() == 0)
        {
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
        if (this.affectedJoins.size() > 0)
        {
            q.append(String.join(" \n", this.affectedJoins));
            q.append(" \n");
        }

        // Filter
        if (this.filterColumns.size() > 0)
        {
            q.append("WHERE ");
            q.append(String.join(" AND ", this.filterColumns));
            q.append(" \n");
        }

        // Group By
        if (this.groupColumns.size() > 0)
        {
            q.append("GROUP BY ");
            q.append(String.join(", ", this.groupColumns));
            q.append(" \n");

            // Having
            if (this.havingColumns.size() > 0)
            {
                q.append("HAVING ");
                q.append(String.join(" AND ", this.havingColumns));
                q.append(" \n");
            }
        }

        // Order by
        if (this.orderColumns.size() > 0)
        {
            q.append("ORDER BY ");
            q.append(String.join(", ", this.orderColumns));
            q.append(" \n");
        }

        // Limit
        if (this.limit > 0)
        {
            q.append("LIMIT ").append(this.limit);
            q.append(" \n");
        }

        // Offset
        if (this.offset != 0)
        {
            q.append("OFFSET ").append(this.offset);
            q.append(" \n");
        }

        return this.sqlToStatement(q.toString());
    }

    private PreparedStatement updateStatement()
    {
        if (this.affectedTables.size() == 0 || this.affectedColumns.size() == 0)
        {
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
        if (this.filterColumns.size() > 0)
        {
            q.append("WHERE ");
            q.append(String.join(" AND ", this.filterColumns));
            q.append(" \n");
        }

        // Limit
        if (this.limit > 0)
        {
            q.append("LIMIT ").append(this.limit);
            q.append(" \n");
        }

        // Offset
        if (this.offset != 0)
        {
            q.append("OFFSET ").append(this.offset);
            q.append(" \n");
        }

        return this.sqlToStatement(q.toString());
    }

    private PreparedStatement deleteStatement()
    {
        if (this.affectedTables.size() == 0)
        {
            return this.db.prepQuery("");
        }

        StringBuilder q = new StringBuilder("DELETE FROM ");

        // Table
        q.append(this.affectedTables.get(0));
        q.append(" \n");

        // Filter
        if (this.filterColumns.size() > 0)
        {
            q.append("WHERE ");
            q.append(String.join(" AND ", this.filterColumns));
            q.append(" \n");
        }

        // Limit
        if (this.limit > 0)
        {
            q.append("LIMIT ").append(this.limit);
            q.append(" \n");
        }

        // Offset
        if (this.offset != 0)
        {
            q.append("OFFSET ").append(this.offset);
            q.append(" \n");
        }

        return this.sqlToStatement(q.toString());
    }

    private PreparedStatement insertStatement()
    {
        if (this.affectedTables.size() == 0 || this.affectedColumns.size() == 0)
        {
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

            if (i + 1 < this.affectedColumns.size())
            {
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

        if (this.type == QUERY_TYPE_INSERT)
        {
            s = this.db.prepQuery(sql, Statement.RETURN_GENERATED_KEYS);
        }
        else
        {
            s = this.db.prepQuery(sql);
        }

        // First, fill in placeholders for insert and update queries
        if (this.affectedColumnDatatypes.size() > 0)
        {
            n = this.affectedColumnDatatypes.size();

            try
            {
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
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
        }

        // Continue replacing placeholders of query filters
        n = this.filterColumns.size();
        i = 0;
        istr = 0;
        iint = 0;
        idbl = 0;

        if (n > 0)
        {
            try
            {
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
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
        }

        // Lastly, do the same with having.

        return s;
    }

    private void setInsertId(PreparedStatement statement)
    {
        try
        {
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next())
            {
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

    /**
     * Executes the current query and returns the number of affected rows.
     *
     * @return Number of affected rows.
     */
    public int executeUpdate()
    {
        int numAffected = 0;
        PreparedStatement statement;
        try
        {
            statement = this.toStatement();
            numAffected = statement.executeUpdate();

            if (this.type == QUERY_TYPE_INSERT)
            {
                this.setInsertId(statement);
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

        return numAffected;
    }

    /**
     * Executes the current query and returns the result set.
     *
     * @return Result set from SELECT query.
     */
    public ResultSet executeQuery()
    {
        ResultSet rs = null;
        PreparedStatement statement;

        try
        {
            statement = this.toStatement();
            rs = this.toStatement().executeQuery();

            if (this.type == QUERY_TYPE_INSERT)
            {
                this.setInsertId(statement);
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

        return rs;
    }
}
