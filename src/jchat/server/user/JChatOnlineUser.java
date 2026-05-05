package jchat.server.user;

import jchat.services.database.records.JChatUser;

import java.net.Socket;

public class JChatOnlineUser
{
    private JChatUser user;
    private Socket userSocket;

    public JChatOnlineUser(Socket userSocket, JChatUser user)
    {

    }

    public void sendMessage(String message)
    {

    }

    public String getUsername()
    {
        return null;
    }
}
