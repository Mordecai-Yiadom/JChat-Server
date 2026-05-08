package jchat.core.net.protocol.tcp;


import jchat.core.net.entity.JChatTextMessage;

public class JChatClientMessagePacket
{
    public static JChatTCPPacket create(JChatTextMessage message)
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.CLIENT_GENERATED_MESSAGE)
                .setPayload(message)
                .build();
    }

//    public enum MetaData implements JChatTCPPacket.MetaData
//    {
//        SENDER("Sender", JChatTCPPacket.MetaDataSupportedTypes.STRING),;
//
//        private final String tag;
//        private final JChatTCPPacket.MetaDataSupportedTypes dataType;
//
//        MetaData(String tag, JChatTCPPacket.MetaDataSupportedTypes dataType)
//        {
//            this.tag = tag;
//            this.dataType = dataType;
//        }
//
//        @Override
//        public String getTag()
//        {
//            return tag;
//        }
//
//        @Override
//        public JChatTCPPacket.MetaDataSupportedTypes getType()
//        {
//            return dataType;
//        }
//    }
}
