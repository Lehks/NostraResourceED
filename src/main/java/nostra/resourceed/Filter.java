package nostra.resourceed;

import java.util.LinkedList;
import java.util.List;

public abstract class Filter
{
    private List<Filter> children;
    
    public static Filter not(Filter filter)
    {
        Filter ret = new OperatorNot();
        
        ret.children.add(filter);
        
        return ret;
    }
    
    protected static String quotedOrNull(String string)
    {
        if(string == null)
            return "NULL";
        else
            return "\"" + string + "\""; 
    }
    
    public Filter()
    {
        this.children = new LinkedList<Filter>();
    }
    
    private Filter addOperator(Filter operator, Filter filter)
    {
        children.add(operator);
        children.add(filter);
        
        return this;
    }

    private Filter addNotOperator(Filter operator, Filter filter)
    {
        children.add(operator);
        children.add(new OperatorNot());
        children.add(filter);
        
        return this;
    }
    
    public Filter and(Filter filter)
    {
        return addOperator(new OperatorAnd(), filter);
    }

    public Filter or(Filter filter)
    {
        return addOperator(new OperatorOr(), filter);
    }

    public Filter andNot(Filter filter)
    {
        return addNotOperator(new OperatorAnd(), filter);
    }

    public Filter orNot(Filter filter)
    {
        return addNotOperator(new OperatorAnd(), filter);
    }
    
    public String generateSQL()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(generateSQLImpl());
        
        for(Filter filter: children)
        {
            //put all children in parenthesis
            if(filter.children.size() > 0)
                builder.append("(");
                
            builder.append(filter.generateSQL());

            //put all children in parenthesis
            if(filter.children.size() > 0)
                builder.append(")");
        }
        
        return builder.toString();
    }
    
    protected abstract String generateSQLImpl();
    
    private static class OperatorAnd extends Filter
    {
        @Override
        public String generateSQLImpl()
        {
            return " AND ";
        }
    }
    
    private static class OperatorOr extends Filter
    {
        @Override
        public String generateSQLImpl()
        {
            return " OR ";
        }
    }

    private static class OperatorNot extends Filter
    {
        @Override
        public String generateSQLImpl()
        {
            return " NOT ";
        }
    }
    
}
