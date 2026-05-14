package jchat.core.net.protocol.tcp;

public class JChatUserRegistrationSuccessfulPacket
{
    public static JChatTCPPacket create()
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.USER_ACCOUNT_REGISTRATION_SUCCESSFUL)
                .addMetaData("Filler", "Filler")
                .setPayload("Filler")
                .build();
    }
}
