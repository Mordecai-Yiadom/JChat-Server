package jchat.core.net.protocol.tcp;

import jchat.core.net.entity.JChatUserCredentials;

public class JChatClientLoginRequestPacket
{
    private static final String USERNAME_METADATA = "Username";
    private static final String PASSWORD_METADATA = "Password";

    public static JChatTCPPacket create(String username, String password)
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.CLIENT_CONNECT_REQUEST)
                .addMetaData(USERNAME_METADATA, username)
                .addMetaData(PASSWORD_METADATA, password)
                .setPayload("Filler")
                .build();
    }

    public static JChatUserCredentials parseUserLoginCredentials(JChatTCPPacket packet)
    {
        return new JChatUserCredentials(
                packet.getMetaData(USERNAME_METADATA),
                packet.getMetaData(PASSWORD_METADATA));
    }


}
