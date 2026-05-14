package jchat.services.registration;

import jchat.core.util.ConfigFileParser;
import jchat.services.database.DatabaseContextProvider;
import jchat.services.database.MySQLContextProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JChatUserRegistrationService
{
    private DatabaseContextProvider databaseContext;

    public JChatUserRegistrationService()
    {
        databaseContext = MySQLContextProvider.createDefault(true);
    }

    private static int generateID()
    {
        return Math.abs(UUID.randomUUID().hashCode());
    }

    public boolean registerNewUser(String username, String password)
    {
        String updateCommand = String.format("INSERT INTO jchat.users VALUES(%d, \"%s\", \"%s\")",
                generateID(),
                username,
                password);

        int result = databaseContext.update(updateCommand);
        return (result != -1);
    }
}
