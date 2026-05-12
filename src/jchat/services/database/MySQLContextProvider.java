package jchat.services.database;

import com.mysql.cj.xdevapi.Table;
import jchat.core.util.ConfigFileParser;

import java.sql.*;

public class MySQLContextProvider implements DatabaseContextProvider
{

    private Connection connection;
    private String user;
    private String password;
    private String url;

    public MySQLContextProvider(String url, String user, String password)
    {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static MySQLContextProvider createDefault(boolean autoConnect)
    {
        ConfigFileParser parser = new ConfigFileParser(".env");
        String username = parser.getString("RDS-User");
        String password = parser.getString("RDS-Password");
        String url = parser.getString("RDS-Connection-String");

       MySQLContextProvider context = new MySQLContextProvider(url, username, password);
       if(autoConnect) context.connect();
       return context;
    }

    @Override
    public void connect()
    {
        try
        {
            connection = DriverManager.getConnection(url, user, password);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void disconnect()
    {
        try
        {
            connection.close();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public ResultSet query(String query)
    {
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeQuery();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int update(String update)
    {
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            return preparedStatement.executeUpdate();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        return -1;
    }
}
