package jchat.services.authentication;

import jchat.core.net.entity.JChatUser;
import jchat.core.util.ConfigFileParser;
import jchat.services.database.DatabaseContextProvider;
import jchat.services.database.MySQLContextProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JChatUserAuthService
{
    private DatabaseContextProvider context;

    public JChatUserAuthService()
    {
        context = MySQLContextProvider.createDefault(true);
    }

    public boolean authenticateUserCredentials(String username, String password)
    {
        String query =
                String.format("SELECT user_id FROM jchat.users WHERE username=\"%s\" AND password=\"%s\"",
                        username, password);
        ResultSet resultSet = context.query(query);

        try
        {
            resultSet.next();
            int userId = resultSet.getInt("user_id");
            return true;
        }
        catch(SQLException ex)
        {
            if(!ex.getMessage().equalsIgnoreCase("Illegal operation on empty result set."))
                ex.printStackTrace();
        }
        return false;
    }

    public int getUserId(String username)
    {
        String query =
                String.format("SELECT user_id FROM jchat.users WHERE username=\"%s\"", username);
        ResultSet resultSet = context.query(query);

        try
        {
            resultSet.next();
            return resultSet.getInt("user_id");
        }
        catch(SQLException ex)
        {
            if(!ex.getMessage().equalsIgnoreCase("Illegal operation on empty result set."))
                ex.printStackTrace();
        }

        return Integer.MIN_VALUE;
    }
}
