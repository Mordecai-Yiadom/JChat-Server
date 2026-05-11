package jchat.server;

import jchat.core.net.entity.JChatTextMessage;
import jchat.core.net.protocol.JChatProtocolUtil;
import jchat.core.net.protocol.tcp.JChatClientMessagePacket;
import jchat.core.net.protocol.tcp.JChatTCPPacket;
import jchat.server.user.JChatOnlineUser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JChatServer
{
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private boolean isRunning;
    private String name;

    private Set<JChatOnlineUser> ONLINE_USERS;

    private Thread listenThread;

    private static final String DEFAULT_SERVER_NAME = "JChatServer";

    public JChatServer()
    {
        this(DEFAULT_SERVER_NAME);
    }

    public JChatServer(String name)
    {
        ONLINE_USERS = new HashSet<>();
        this.name = name;
    }


    public void start(int port) {
        isRunning = true;

        try {
            createServerSocket(port);
            multiplexSocketChannels();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void stop() {
        isRunning = false;
    }

    private void createServerSocket(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
    }

    public void broadcast(String message) {
        for (JChatOnlineUser user : ONLINE_USERS) {
            user.sendMessage(message);
        }
    }


    private void multiplexSocketChannels() throws IOException {
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (isRunning) {
            try {
                selector.select();

                for (SelectionKey key : selector.selectedKeys())
                {
                    if (key.isAcceptable())
                    {
                        connectNewUser();
                    }

                    if (key.isReadable())
                    {
                        processPackets((SocketChannel) key.channel());
                    }
                }

                selector.selectedKeys().clear();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void connectNewUser() {
        try {
            SocketChannel clientSocket = serverSocketChannel.accept();
            clientSocket.configureBlocking(false);
            clientSocket.register(selector, SelectionKey.OP_READ);

            JChatOnlineUser user = new JChatOnlineUser(clientSocket, null);

            if (user == null) return;
            broadcast("A new user has joined.");

            ONLINE_USERS.add(user);
            user.sendMessage("Welcome!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void processPackets(SocketChannel socketChannel)
    {
        try
        {
            ArrayList<JChatTCPPacket> packets = JChatProtocolUtil.readJChatTCPPacket(socketChannel);

            for (JChatTCPPacket packet : packets)
            {
                switch(packet.getPacketCode())
                {
                    case CLIENT_GENERATED_MESSAGE:
                        onTextMessageReceived(packet, socketChannel);
                        break;
                }
            }
        }
        catch (IOException ex)
        {

            if(ex.getMessage().equalsIgnoreCase("Connection Reset"))
                disconnectUser(socketChannel);
            else
                ex.printStackTrace();
        }
    }


    private void onTextMessageReceived(JChatTCPPacket packet, SocketChannel socketChannel)
    {
        for(JChatOnlineUser user : ONLINE_USERS)
        {
            if(socketChannel.equals(user.getSocket())) continue;
            user.sendPacket(packet);
        }
    }


    private void disconnectUser(SocketChannel socketChannel)
    {

        for(JChatOnlineUser user : ONLINE_USERS)
        {
            if(user.getSocket().equals(socketChannel))
            {
                ONLINE_USERS.remove(user);

                try
                {
                    socketChannel.close();
                }
                catch(IOException ex)
                {
                    ex.printStackTrace();
                }

                broadcast("A user has disconnected.");
                System.out.println("[Server INFO] User disconnected.");

                return;
            }
        }

    }
}
