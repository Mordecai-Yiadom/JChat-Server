package jchat.services.database;

public interface DatabaseContextProvider
{
    void connect();
    void disconnect();

    QueryResult query(String query);
}
