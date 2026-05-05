package jchat.server;

import jchat.server.user.JChatOnlineUser;

import java.io.IOException;
import java.net.*;
import java.util.Set;

public class JChatServer
{
    private ServerSocket listenSocket;

    private boolean isRunning;

    private Set<JChatOnlineUser> ONLINE_USERS;


    public JChatServer(int port)
    {
        try
        {
            listenSocket = new ServerSocket(port);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void start(String[] args)
    {
        isRunning = true;

        Thread listenThread = new Thread(()->
        {
            listenForClients();
        });

        listenThread.start();

    }

    public void stop()
    {
        isRunning = false;
    }


    public void listenForClients()
    {
        System.out.println("Waiting For Client Connections");

        while(isRunning)
        {
            try
            {
                Socket clientSocket = listenSocket.accept();




            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void broadcast(String message)
    {
        for(JChatOnlineUser user : ONLINE_USERS)
        {
            user.sendMessage(message);
        }
    }


}
