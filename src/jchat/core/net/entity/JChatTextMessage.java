package jchat.core.net.entity;

public class JChatTextMessage implements JChatNetworkEntity
{
    private String sender;
    private String message;

    public JChatTextMessage(String sender, String message)
    {
        this.sender = sender;
        this.message = message;
    }


    public String getSender()
    {
        return sender;
    }

    public String getMessage()
    {
        return message;
    }
}
