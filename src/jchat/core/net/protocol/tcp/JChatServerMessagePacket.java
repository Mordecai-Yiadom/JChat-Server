package jchat.core.net.protocol.tcp;

import jchat.core.net.entity.JChatTextMessage;

public class JChatServerMessagePacket
{
    private static final String SENDER_METADATA_TAG =  "Sender";

    public static JChatTCPPacket create(JChatTextMessage message)
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.SERVER_GENERATED_MESSAGE)
                .addMetaData(SENDER_METADATA_TAG, message.getSender())
                .setPayload(message.getMessage())
                .build();
    }
}
