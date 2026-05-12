package jchat.core.net.protocol.tcp;

public class JChatClientLoginRejectedPacket
{
    public static JChatTCPPacket create()
    {
        return new JChatTCPPacket.Builder()
                .addMetaData("Filler", "Filler")
                .setPacketCode(JChatTCPPacket.PacketCode.CLIENT_CONNECTION_REJECTED_RESPONSE)
                .setPayload("Filler")
                .build();
    }
}
