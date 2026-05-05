package jchat.services.database;

import java.sql.Connection;

public class MySQLContextProvider implements DatabaseContextProvider
{
    private Connection connection;

    public MySQLContextProvider()
    {}

    @Override
    public void connect()
    {

    }

    @Override
    public void disconnect()
    {

    }

    @Override
    public QueryResult query(String query)
    {
        return null;
    }
}
