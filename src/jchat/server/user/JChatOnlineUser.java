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
    }


    public boolean sendMessage(String message)
    {
        try
        {
            JChatProtocolUtil.sendJChatTCPPacket(JChatClientMessagePacket.create(
                    new JChatTextMessage("SERVER-123931023", message)), socket);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
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
        return null;
    }
}
