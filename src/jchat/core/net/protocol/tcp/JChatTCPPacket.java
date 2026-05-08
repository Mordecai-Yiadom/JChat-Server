package jchat.core.net.protocol.tcp;

import jchat.core.net.protocol.JChatProtocolUtil;

import javax.management.loading.ClassLoaderRepository;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class JChatTCPPacket
{
    private static final String PACKET_DELIMITER = "<JCHAT_TCP_PACKET>";
    private static final String PACKET_CODE_DELIMITER = "<PACKET_CODE>";

    private static final String PAYLOAD_DELIMITER = "<PAYLOAD>";


    private PacketCode code;

    private long payloadLength;


    private Serializable payload;

    private byte[] rawPacket;


    private JChatTCPPacket() {}


    public PacketCode getPacketCode()
    {
        return code;
    }

    public long getPayloadLength()
    {
        return payloadLength;
    }

    public Serializable getPayload()
    {
        return payload;
    }

    public byte[] raw()
    {
        return rawPacket;
    }

    private void buildRawPacket()
    {
        StringBuilder packetCodeBuffer = new StringBuilder();
        packetCodeBuffer.append(PACKET_CODE_DELIMITER);
        packetCodeBuffer.append(code.code);
        packetCodeBuffer.append(PACKET_CODE_DELIMITER);

        StringBuilder payloadBuffer = new StringBuilder();
        payloadBuffer.append(PAYLOAD_DELIMITER);
        payloadBuffer.append(JChatProtocolUtil.serializeObjectToCharArray(payload));
        payloadBuffer.append(PAYLOAD_DELIMITER);

        String packetBuffer = String.format("%s%s%s%s",
                PACKET_DELIMITER,
                packetCodeBuffer,
                payloadBuffer,
                PACKET_DELIMITER);

        rawPacket = packetBuffer.getBytes();
    }


    public static class Builder
    {
        private JChatTCPPacket packet;

        public Builder()
        {
            packet = new JChatTCPPacket();
        }


        public Builder setPacketCode(PacketCode code)
        {
            packet.code = code;
            return this;
        }

        public <T extends Serializable> Builder setPayload(T payload)
        {
            packet.payload = payload;
            return this;
        }

        public JChatTCPPacket build()
        {
            packet.buildRawPacket();
            return packet;
        }

    }


    public enum PacketCode implements Serializable
    {
        /****************************
            Level 100: Connection
         ***************************/

        CLIENT_CONNECTION_REQUEST(101),
        CLIENT_DISCONNECT_REQUEST(102),


        /****************************
            Level 200: Data Transfer
         ***************************/

        CLIENT_GENERATED_MESSAGE(201),
        CLIENT_GENERATED_COMMAND(202),
        SERVER_GENERATED_MESSAGE(203),
        ;

        //Level 400: Error
        private final int code;

        PacketCode(int code)
        {
            this.code = code;
        }

        public int code()
        {
            return code;
        }
    }
//
//    public interface MetaData extends Serializable
//    {
//        String getTag();
//        MetaDataSupportedTypes getType();
//    }
//
//
//    public enum MetaDataSupportedTypes
//    {
//        STRING,
//        INTEGER,
//        FLOAT,
//        DOUBLE,
//        BOOLEAN,
//    }
//

//    public enum DefaultMetaData implements MetaData
//    {
//        PAYLOAD_DATATYPE("PayloadDataType", ),;
//
//        private final String tag;
//        private MetaDataSupportedTypes dataType;
//
//        DefaultMetaData(String tag, MetaDataSupportedTypes dataType)
//        {
//            this.tag = tag;
//            this.dataType = dataType;
//        }
//
//        @Override
//        public String getTag() {
//
//            return "";
//        }
//
//        @Override
//        public MetaDataSupportedTypes getType()
//        {
//            return null;
//        }
//    }
}
