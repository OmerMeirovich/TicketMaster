package database;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author OmerMeirovich
 * 
 * Handles the connection to the database.
 */
public class DbConnection{
    private final String db_url = "jdbc:derby://localhost:1527/ticket_master";
    private final String db_username = "master";
    private final String db_password = "123";
    
    private Connection connection = null;
    protected PreparedStatement ps = null; 
    
    protected ResultSet rs = null;
    
    /**
     * connect to the database.
     * 
     * @return connection instance of the database
     * @throws SQLException if any error were thrown when trying to connect
     */
    public Connection connect()throws SQLException{
        DriverManager.registerDriver(new Driver());
        connection = DriverManager.getConnection(db_url,db_username,db_password);
        return connection;
    }
    
    /**
     * disconnect from the database
     * 
     * @throws SQLException if any error were thrown when trying to disconnect
     */
    public void disconnect() throws SQLException{
        if( connection != null)
            connection.close();
        if (ps != null)
            ps.close();
        if(rs != null)
            rs.close();
    }
}
