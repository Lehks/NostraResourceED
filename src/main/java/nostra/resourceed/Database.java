package nostra.resourceed;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 * @author Tobias Kuhn
 */
public class Database implements Closeable
{
    private Connection conn;
    private String path;

    public Database(String path) throws SQLException
    {
        Properties properties = new Properties();
        properties.setProperty("foreign_keys", "ON");

        this.path = path;
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + path, properties);
    }

    public PreparedStatement prepQuery(String sql)
    {
        PreparedStatement statement = null;

        try
        {
            statement = this.conn.prepareStatement(sql);
        }
        catch (SQLException e)
        {
            System.err.println("Could not prepare statement: " + e.getMessage());
        }

        return statement;
    }

    public PreparedStatement prepQuery(String sql, int options)
    {
        PreparedStatement statement = null;

        try
        {
            statement = this.conn.prepareStatement(sql, options);
        }
        catch (SQLException e)
        {
            System.err.println("Could not prepare statement: " + e.getMessage());
        }

        return statement;
    }

    @Override
    public void close()
    {
        if (this.conn == null)
        {
            return;
        }

        try
        {
            this.conn.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

        this.conn = null;
    }

    public String getPath()
    {
        return path;
    }
}
