package jchat.core.net.protocol.tcp;

import jchat.core.net.entity.JChatUserCredentials;

public class JChatUserRegistrationRequestPacket
{
    private static final String REQUESTED_USERNAME_META_TAG = "RequestedUsername";
    private static final String REQUESTED_PASSWORD_META_TAG = "RequestedPassword";

    public static JChatTCPPacket create(String requestedUsername, String requestedPassword)
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.USER_ACCOUNT_REGISTRATION_REQUEST)
                .addMetaData(REQUESTED_USERNAME_META_TAG, requestedUsername)
                .addMetaData(REQUESTED_PASSWORD_META_TAG, requestedPassword)
                .setPayload("Filler")
                .build();
    }

    public static JChatUserCredentials parseRequestedCredentials(JChatTCPPacket packet)
    {
        return new JChatUserCredentials(
                packet.getMetaData(REQUESTED_USERNAME_META_TAG),
                packet.getMetaData(REQUESTED_PASSWORD_META_TAG));
    }
}
