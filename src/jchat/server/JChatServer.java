package jchat.server;

import jchat.server.user.JChatOnlineUser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Set;

public class JChatServer
{
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private boolean isRunning;

    private Set<JChatOnlineUser> ONLINE_USERS;

    private Thread listenThread;

    public JChatServer()
    {
        ONLINE_USERS = new HashSet<>();
    }

    public void start(int port)
    {
        isRunning = true;

        try
        {
            createServerSocket(port);
            multiplexSocketChannels();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }



    public void stop()
    {
        isRunning = false;
    }

    private void createServerSocket(int port) throws IOException
    {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
    }


    public void broadcast(String message)
    {
        for(JChatOnlineUser user : ONLINE_USERS)
        {
            user.sendMessage(message);
        }
    }



    private void multiplexSocketChannels() throws IOException
    {
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(isRunning)
        {
            try
            {
                selector.select();

                for(SelectionKey key : selector.selectedKeys())
                {
                    if(key.isAcceptable())
                    {
                        connectNewUser();
                    }

                    if(key.isReadable())
                    {
                        String message = readMessage((SocketChannel) key.channel());

                        if(message == null)
                        {
                            disconnectUser((SocketChannel) key.channel());
                            ((SocketChannel) key.channel()).close();
                        }
                        else System.out.printf("<User> %s\n", message);
                    }
                }

                selector.selectedKeys().clear();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }

    private void connectNewUser()
    {
        try
        {
            SocketChannel clientSocket = serverSocketChannel.accept();
            clientSocket.configureBlocking(false);
            clientSocket.register(selector, SelectionKey.OP_READ);

            JChatOnlineUser user = new JChatOnlineUser(clientSocket, null);

            if(user == null) return;
            ONLINE_USERS.add(user);

            broadcast("A new user has joined.");

            user.sendMessage("Welcome User");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private String readMessage(SocketChannel socketChannel)
    {
        StringBuilder message = new StringBuilder();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();

        try
        {
            socketChannel.read(byteBuffer);

            for(int i = 0; i < byteBuffer.array().length; i++)
            {
                char c = (char) byteBuffer.array()[i];
                if(c == '\0') break;
                message.append(c);
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            return null;
        }

        return message.toString();
    }

    private void disconnectUser(SocketChannel socketChannel)
    {

        for(JChatOnlineUser user : ONLINE_USERS)
        {
            if(user.getSocket().equals(socketChannel))
            {
                ONLINE_USERS.remove(user);
                System.out.println("[Server INFO] User disconnected.");
                return;
            }
        }
    }
}
