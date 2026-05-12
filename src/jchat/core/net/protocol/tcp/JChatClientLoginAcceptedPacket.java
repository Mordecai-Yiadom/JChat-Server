package jchat.core.net.protocol.tcp;

public class JChatClientLoginAcceptedPacket
{
    public static JChatTCPPacket create()
    {
        return new JChatTCPPacket.Builder()
                .addMetaData("Filler", "Filler")
                .setPacketCode(JChatTCPPacket.PacketCode.CLIENT_CONNECTION_ACCEPTED_RESPONSE)
                .setPayload("Filler")
                .build();
    }
}
