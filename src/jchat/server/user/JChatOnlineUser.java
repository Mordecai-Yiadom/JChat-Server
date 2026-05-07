package jchat.server.user;

import jchat.services.database.records.JChatUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

public class JChatOnlineUser
{
    private JChatUser user;
    private SocketChannel socket;

    public JChatOnlineUser(SocketChannel socket, JChatUser user)
    {
        this.socket = socket;
    }

    public void sendMessage(String message)
    {
        try
        {
            ByteBuffer byteBuffer = ByteBuffer.allocate(message.getBytes().length);
            byteBuffer.clear().put(message.getBytes()).flip();

            while(byteBuffer.hasRemaining())
            {
                socket.write(byteBuffer);
            }


            System.out.printf("[Server Info] Message sent to %s\n", socket.getRemoteAddress().toString());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
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
