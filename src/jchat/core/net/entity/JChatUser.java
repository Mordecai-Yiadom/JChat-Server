package jchat.core.net.entity;

public class JChatUser
{
    private int id;
    private String username;

    public JChatUser(int id, String username)
    {
        this.id = id;
        this.username = username;
    }

    public int getId()
    {
        return id;
    }

    public String getUsername()
    {
        return username;
    }
}
