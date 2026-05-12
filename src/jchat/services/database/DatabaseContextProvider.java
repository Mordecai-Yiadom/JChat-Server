package jchat.services.database;

import java.sql.ResultSet;

public interface DatabaseContextProvider
{
    void connect();
    void disconnect();

    ResultSet query(String query);
    int update(String update);

}
