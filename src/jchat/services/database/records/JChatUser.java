package jchat.services.database.records;

public record JChatUser (int id, String username)
{
    public static boolean isValid(JChatUser user)
    {
        return true;
    }

    public static boolean isUsernameInUse(String username)
    {
        return false;
    }


}
