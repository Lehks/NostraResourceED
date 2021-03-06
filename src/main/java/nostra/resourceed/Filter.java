package nostra.resourceed;

import java.util.LinkedList;
import java.util.List;

/**
 * A class that is the parent class of all filters.
 * 
 * This class also implements the logic to concatenate multiple filters.
 * 
 * @author Lukas Reichmann
 */
public abstract class Filter
{
    /**
     * The filters that are connected to this filter.
     */
    private List<Filter> children;

    /**
     * Creates a NOT operation and connects it to the passed filter.
     * 
     * @param filter The filter to connect to.
     * @return The created filter.
     */
    public static Filter not(Filter filter)
    {
        Filter ret = new OperatorNot();

        ret.children.add(filter);

        return ret;
    }

    /**
     * Creates a statement that is similar to "column = string". If, however, string
     * is NULL, the statement will be "column IS NULL".
     * 
     * @param column The column to compare the string to.
     * @param string The string to compare to.
     * @return The modified string.
     */
    protected static String isEquals(String column, String string)
    {
        if (string != null)
            return column + " = \"" + string + "\"";
        else
            return column + " IS NULL";
    }

    /**
     * The default constructor.
     */
    public Filter()
    {
        this.children = new LinkedList<Filter>();
    }

    /**
     * First adds an operator and a regular filter.
     * 
     * @param operator The operator.
     * @param filter   The filter.
     * @return The instance that the method was called on.
     */
    private Filter addOperator(Filter operator, Filter filter)
    {
        children.add(operator);
        children.add(filter);

        return this;
    }

    /**
     * First adds an operator, then a NOT operator and then a regular filter.
     * 
     * @param operator The operator.
     * @param filter   The filter.
     * @return The instance that the method was called on.
     */
    private Filter addNotOperator(Filter operator, Filter filter)
    {
        children.add(operator);
        children.add(new OperatorNot());
        children.add(filter);

        return this;
    }

    /**
     * Connects two filters using AND.
     * 
     * @param filter The filter to connect to.
     * @return The instance that this method was called on.
     */
    public Filter and(Filter filter)
    {
        return addOperator(new OperatorAnd(), filter);
    }

    /**
     * Connects two filters using OR.
     * 
     * @param filter The filter to connect to.
     * @return The instance that this method was called on.
     */
    public Filter or(Filter filter)
    {
        return addOperator(new OperatorOr(), filter);
    }

    /**
     * Connects two filters using AND NOT.
     * 
     * @param filter The filter to connect to.
     * @return The instance that this method was called on.
     */
    public Filter andNot(Filter filter)
    {
        return addNotOperator(new OperatorAnd(), filter);
    }

    /**
     * Connects two filters using AND NOT.
     * 
     * @param filter The filter to connect to.
     * @return The instance that this method was called on.
     */
    public Filter orNot(Filter filter)
    {
        return addNotOperator(new OperatorAnd(), filter);
    }

    /**
     * Generates the SQL statement for this filter.
     * 
     * @return The SQL statement.
     */
    public String generateSQL()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(generateSQLImpl());

        for (Filter filter : children)
        {
            // put all children in parenthesis
            if (filter.children.size() > 0)
                builder.append("(");

            builder.append(filter.generateSQL());

            // put all children in parenthesis
            if (filter.children.size() > 0)
                builder.append(")");
        }

        return builder.toString();
    }

    protected abstract String generateSQLImpl();

    /**
     * A filter that represents the AND operation.
     */
    private static class OperatorAnd extends Filter
    {
        @Override
        public String generateSQLImpl()
        {
            return " AND ";
        }
    }

    /**
     * A filter that represents the OR operation.
     */
    private static class OperatorOr extends Filter
    {
        @Override
        public String generateSQLImpl()
        {
            return " OR ";
        }
    }

    /**
     * A filter that represents the NOT operation.
     */
    private static class OperatorNot extends Filter
    {
        @Override
        public String generateSQLImpl()
        {
            return " NOT ";
        }
    }

}
