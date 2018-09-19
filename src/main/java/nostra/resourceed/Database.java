package nostra.resourceed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database
{
    private static Database instance;
    private static final String DBNAME = "nostra";
    private Connection conn;
    private QueryBuilder query;


    private Database() throws SQLException
    {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + DBNAME + ".db");
    }


    public static Database getInstance()
    {
        if (Database.instance == null) {
            try {
                Database.instance = new Database();
            }
            catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return Database.instance;
    }


    public static QueryBuilder getQuery()
    {
        return new QueryBuilder(Database.getInstance());
    }


    public PreparedStatement prepQuery(String sql)
    {
        PreparedStatement statement = null;

        try {
            statement = this.conn.prepareStatement(sql);
        }
        catch(SQLException e) {
            System.err.println("Could not prepare statement: " + e.getMessage());
        }

        return statement;
    }
    public PreparedStatement prepQuery(String sql, int options)
    {
        PreparedStatement statement = null;

        try {
            statement = this.conn.prepareStatement(sql, options);
        }
        catch(SQLException e) {
            System.err.println("Could not prepare statement: " + e.getMessage());
        }

        return statement;
    }


    public void close()
    {
        if (this.conn == null) {
            return;
        }

        try {
            this.conn.close();
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }

        this.conn = null;
    }
}
