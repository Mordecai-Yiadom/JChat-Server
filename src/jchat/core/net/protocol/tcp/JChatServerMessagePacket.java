package jchat.core.net.protocol.tcp;

import jchat.core.net.entity.JChatTextMessage;

public class JChatServerMessagePacket
{
    public static JChatTCPPacket create(JChatTextMessage message)
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.SERVER_GENERATED_MESSAGE)
                .addMetaData("SENDER", message.getSender())
                .setPayload(message.getMessage())
                .build();
    }
}
