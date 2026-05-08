package jchat.services.database.records;

public record JChatUserRecord(int id, String username)
{
    public static boolean isValid(JChatUserRecord user)
    {
        return true;
    }

    public static boolean isUsernameInUse(String username)
    {
        return false;
    }


}
