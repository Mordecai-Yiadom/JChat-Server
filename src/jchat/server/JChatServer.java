package jchat.server;

import jchat.core.net.entity.JChatTextMessage;
import jchat.core.net.entity.JChatUser;
import jchat.core.net.entity.JChatUserCredentials;
import jchat.core.net.protocol.JChatProtocolUtil;
import jchat.core.net.protocol.tcp.*;
import jchat.server.user.JChatOnlineUser;
import jchat.services.authentication.JChatUserAuthService;
import jchat.services.registration.JChatUserRegistrationService;


import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JChatServer
{
    private static final String DEFAULT_SERVER_NAME = "JChatServer";

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private boolean isRunning;
    private String name;

    private Set<JChatOnlineUser> ONLINE_USERS;


    private JChatUserAuthService userAuthService;
    private JChatUserRegistrationService userRegistrationService;

    public JChatServer()
    {
        this(DEFAULT_SERVER_NAME);
    }

    public JChatServer(String name)
    {
        ONLINE_USERS = new HashSet<>();
        this.name = name;
        userAuthService = new JChatUserAuthService();
        userRegistrationService = new JChatUserRegistrationService();
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
        for (JChatOnlineUser user : ONLINE_USERS)
        {
            sendMessage(user, message);
        }
    }


    private void multiplexSocketChannels() throws IOException
    {
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (isRunning)
        {
            try
            {
                selector.select();

                for (SelectionKey key : selector.selectedKeys())
                {
                    if (key.isAcceptable())
                    {
                        acceptConnection();
                    }

                    if (key.isReadable())
                    {
                        processPackets((SocketChannel) key.channel());
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

    private void acceptConnection()
    {
        try
        {
            SocketChannel clientSocket = serverSocketChannel.accept();
            clientSocket.configureBlocking(false);
            clientSocket.register(selector, SelectionKey.OP_READ);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }


    private void disconnectConnection(SocketChannel socketChannel)
    {
        try
        {
            socketChannel.close();
            System.out.println("[Server INFO] A client has disconnected.");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void connectNewUser(SocketChannel socketChannel, JChatUser user)
    {
        JChatOnlineUser onlineUser = new JChatOnlineUser(socketChannel, user);
        if (user == null) return;
        ONLINE_USERS.add(onlineUser);
    }

    private void disconnectUser(JChatOnlineUser user)
    {
        disconnectConnection(user.getSocket());
        ONLINE_USERS.remove(user);

        broadcast(String.format("%s has disconnected.", user.getUsername()));
        System.out.printf("[Server INFO] User %s(id=%d) has disconnected.\n", user.getUsername(), user.getId());
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

                    case CLIENT_CONNECT_REQUEST:
                        onClientConnectionRequest(packet, socketChannel);
                        break;

                    case USER_ACCOUNT_REGISTRATION_REQUEST:
                        onUserAccountRegistrationRequest(packet, socketChannel);
                        break;
                }
            }
        }
        catch (IOException ex)
        {
            if(ex.getMessage().equalsIgnoreCase("Connection Reset"))
            {
                JChatOnlineUser user = getUser(socketChannel);

                if(user == null)
                    disconnectConnection(socketChannel);
                else
                    disconnectUser(user);
            }

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

        System.out.printf("[Server INFO] %s sent smessage: \"%s\"\n",
                packet.getMetaData("Sender"), packet.getPayload());
    }

    public boolean sendMessage(JChatOnlineUser user, String message)
    {
        try
        {
            JChatProtocolUtil.sendJChatTCPPacket(JChatServerMessagePacket.create(
                    new JChatTextMessage(this.name, message)), user.getSocket());
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    private void onClientConnectionRequest(JChatTCPPacket packet, SocketChannel socketChannel) throws IOException
    {
        JChatUserCredentials credentials = JChatClientLoginRequestPacket.parseUserLoginCredentials(packet);
        if(userAuthService.authenticateUserCredentials(credentials.username(), credentials.password()))
        {
            JChatProtocolUtil.sendJChatTCPPacket(JChatClientLoginAcceptedPacket.create(), socketChannel);

            JChatUser user = new JChatUser(userAuthService.getUserId(credentials.username()), credentials.username());
            connectNewUser(socketChannel, user);

            broadcast(String.format("%s has connected.", user.getUsername()));
        }
        else
        {
            JChatProtocolUtil.sendJChatTCPPacket(JChatClientLoginRejectedPacket.create(), socketChannel);
            disconnectConnection(socketChannel);
        }

    }

    private void onUserAccountRegistrationRequest(JChatTCPPacket packet, SocketChannel socketChannel) throws IOException
    {
        JChatUserCredentials credentials = JChatUserRegistrationRequestPacket.parseRequestedCredentials(packet);

        if(userRegistrationService.registerNewUser(credentials.username(), credentials.password()))
        {
            JChatProtocolUtil.sendJChatTCPPacket(JChatUserRegistrationSuccessfulPacket.create(), socketChannel);
            System.out.println("[Service(User-Registration) INFO]: Successfully Registered new account.");
        }
        else
        {
            JChatProtocolUtil.sendJChatTCPPacket(
                    JChatUserRegistrationFailedPacket.create("Username is already taken"), socketChannel);
            System.out.println("[Service(User-Registration) INFO]: Failed to register new account.");
        }
    }

    private JChatOnlineUser getUser(String username)
    {
        for(JChatOnlineUser user : ONLINE_USERS)
        {
            if(user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    private JChatOnlineUser getUser(SocketChannel socketChannel)
    {
        for(JChatOnlineUser user : ONLINE_USERS)
        {
            if(user.getSocket().equals(socketChannel))
                return user;
        }
        return null;
    }



}
