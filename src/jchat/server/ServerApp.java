package jchat.server;

import jchat.core.util.ConfigFileParser;

public class ServerApp
{
    private static JChatServer server;

    public static void main(String[] args)
    {
        System.out.println("Running JChat Server");
        ConfigFileParser parser = new ConfigFileParser(".env");

        server = new JChatServer(parser.getInteger("Listen-Port"));
        server.start(args);
    }
}
