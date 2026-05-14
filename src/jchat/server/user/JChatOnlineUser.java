package jchat.server.user;

import jchat.core.net.entity.JChatTextMessage;
import jchat.core.net.entity.JChatUser;
import jchat.core.net.protocol.JChatProtocolUtil;
import jchat.core.net.protocol.tcp.JChatClientMessagePacket;
import jchat.core.net.protocol.tcp.JChatTCPPacket;


import java.io.IOException;
import java.nio.channels.SocketChannel;

public class JChatOnlineUser
{
    private JChatUser user;
    private SocketChannel socket;

    public JChatOnlineUser(SocketChannel socket, JChatUser user)
    {
        this.socket = socket;
        this.user = user;
    }

    public boolean sendPacket(JChatTCPPacket packet)
    {
        try
        {
            JChatProtocolUtil.sendJChatTCPPacket(packet, socket);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    public SocketChannel getSocket()
    {
        return socket;
    }

    public String getUsername()
    {
        if(user == null) return null;
        return user.getUsername();
    }

    public int getId()
    {
        if(user == null) return Integer.MIN_VALUE;
        return user.getId();
    }

}
