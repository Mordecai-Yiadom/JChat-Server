package jchat.core.net.protocol;

import jchat.core.net.protocol.tcp.JChatTCPPacket;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class JChatProtocolUtil
{
    public static char[] byteToCharArray(byte[] byteArray)
    {
        if(byteArray == null) return null;

        char[] charArray = new char[byteArray.length];

        for(int i = 0; i < byteArray.length; i++)
        {
            charArray[i] = (char) byteArray[i];
        }

        return charArray;
    }

    public static byte[] charToByteArray(char[] charArray)
    {
        if(charArray == null) return null;

        byte[] byteArray = new byte[charArray.length];

        for(int i = 0; i < charArray.length; i++)
        {
            byteArray[i] = (byte) charArray[i];
        }

        return byteArray;
    }

    public static <T extends Serializable> byte[] serializeObject(T object)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();

            return byteArrayOutputStream.toByteArray();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();

        }
        return null;
    }

    public static <T extends Serializable> char[] serializeObjectToCharArray(T object)
    {
       return byteToCharArray(serializeObject(object));
    }

    public static void sendJChatTCPPacket(JChatTCPPacket packet, SocketChannel socketChannel) throws IOException
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(packet.raw().length);
        byteBuffer.clear().put(packet.raw()).flip();

        while(byteBuffer.hasRemaining())
        {
            socketChannel.write(byteBuffer);
        }
    }
}
