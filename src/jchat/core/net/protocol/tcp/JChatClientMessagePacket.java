package jchat.core.net.protocol.tcp;


import jchat.core.net.entity.JChatTextMessage;

public class JChatClientMessagePacket
{
    private static final String SENDER_METADATA_TAG =  "Sender";
    public static JChatTCPPacket create(JChatTextMessage message)
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.CLIENT_GENERATED_MESSAGE)
                .addMetaData(SENDER_METADATA_TAG, message.getSender())
                .setPayload(message.getMessage())
                .build();
    }

    public static JChatTextMessage parseTextMessage(JChatTCPPacket packet)
    {
        String sender = packet.getMetaData(SENDER_METADATA_TAG);
        String message = packet.getPayload();

        return new JChatTextMessage(sender, message);
    }

}
