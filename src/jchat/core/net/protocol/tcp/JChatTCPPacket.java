package jchat.core.net.protocol.tcp;

import jchat.core.net.protocol.JChatProtocolUtil;

import javax.management.loading.ClassLoaderRepository;
import javax.xml.transform.stream.StreamSource;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JChatTCPPacket
{
    private static final String PACKET_DELIMITER = "<JCHAT_TCP_PACKET>";
    private static final String PACKET_CODE_DELIMITER = "<PACKET_CODE>";

    private static final String METADATA_DELIMITER = "<METADATA>";
    private static final String METADATA_ENTRY_DELIMITER = ";";
    private static final String METADATA_VALUE_ASSIGNMENT_DELIMITER = "=";

    private static final String PAYLOAD_DELIMITER = "<PAYLOAD>";


    private PacketCode code;
    private String payload;
    private byte[] rawPacket;
    private Map<String, String> metaDataMap;


    private JChatTCPPacket()
    {
        metaDataMap = new HashMap<>();
    }


    public PacketCode getPacketCode()
    {
        return code;
    }

    public String getPayload()
    {
        return payload;
    }

    public byte[] raw()
    {
        return rawPacket;
    }

    public String getMetaData(String key)
    {
        return metaDataMap.get(key);
    }

    private void addMetaData(String key, String value)
    {
        if(key == null) return;
        metaDataMap.put(key, value);
    }

    private void buildRawPacket()
    {
        //Build PacketCode
        StringBuilder packetCodeBuffer = new StringBuilder();
        packetCodeBuffer.append(PACKET_CODE_DELIMITER);
        packetCodeBuffer.append(code.code);
        packetCodeBuffer.append(PACKET_CODE_DELIMITER);

        //Build MetaData
        StringBuilder metaDataBuffer = new StringBuilder();
        metaDataBuffer.append(METADATA_DELIMITER);
        for(String key : metaDataMap.keySet())
        {
            metaDataBuffer.append(String.format("%s%s%s%s",
                    key,
                    METADATA_VALUE_ASSIGNMENT_DELIMITER,
                    metaDataMap.get(key),
                    METADATA_ENTRY_DELIMITER));
        }
        metaDataBuffer.append(METADATA_DELIMITER);

        //Build Payload
        StringBuilder payloadBuffer = new StringBuilder();
        payloadBuffer.append(PAYLOAD_DELIMITER);
        payloadBuffer.append(payload);
        payloadBuffer.append(PAYLOAD_DELIMITER);


        String packetBuffer = String.format("%s%s%s%s%s",
                PACKET_DELIMITER,
                packetCodeBuffer,
                metaDataBuffer,
                payloadBuffer,
                PACKET_DELIMITER);

        rawPacket = packetBuffer.getBytes();
    }

    public static ArrayList<JChatTCPPacket> parsePackets(byte[] rawPacket)
    {
        ArrayList<JChatTCPPacket> parsedPackets = new ArrayList<>();

        //Convert rawPacket to String ready for parsing
        StringBuilder rawPacketBuffer = new StringBuilder();
        rawPacketBuffer.append(JChatProtocolUtil.byteToCharArray(rawPacket));

        //Determine potential packet count
        String[] splitRawPackets = rawPacketBuffer.toString().split(PACKET_DELIMITER);

        for(String rawStr : splitRawPackets)
        {
            if(rawStr.startsWith(PACKET_CODE_DELIMITER))
            {
                parsedPackets.add(parsePacket(rawStr));
            }
        }

        return parsedPackets;
    }


    private static JChatTCPPacket parsePacket(String rawPacket)
    {
        //Parse PacketCode
        int rawPacketCode = Integer.parseInt(rawPacket.split(PACKET_CODE_DELIMITER)[1]);

        PacketCode packetCode = PacketCode.get(rawPacketCode);
        if(packetCode == null) return null;

        //Parse MetaData
        Map<String, String> metaDataMap = new HashMap<>();
        String rawMetaData = rawPacket.split(METADATA_DELIMITER)[1];

        for(String s : rawMetaData.split(METADATA_ENTRY_DELIMITER))
        {
            String key = s.split(METADATA_VALUE_ASSIGNMENT_DELIMITER)[0];
            String value = s.split(METADATA_VALUE_ASSIGNMENT_DELIMITER)[1];

            metaDataMap.put(key, value);
        }

        //Parse payload
        String payload = "";
        if(rawPacket.split(PAYLOAD_DELIMITER).length > 0)
            payload = rawPacket.split(PAYLOAD_DELIMITER)[1];

        //Create JChatTCPPacket Object
        return new JChatTCPPacket.Builder()
                .setPacketCode(packetCode)
                .addMetaData(metaDataMap)
                .setPayload(payload)
                .build();
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

        public Builder setPayload(String payload)
        {
            packet.payload = payload;
            return this;
        }

        public Builder addMetaData(String key, String value)
        {
            packet.addMetaData(key, value);
            return this;
        }

        public Builder addMetaData(Map<String, String> dataMap)
        {
            if(dataMap == null) return this;

            for(String key : dataMap.keySet())
            {
                packet.addMetaData(key, dataMap.get(key));
            }
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

        CLIENT_CONNECT_REQUEST(101),
        CLIENT_DISCONNECT_REQUEST(102),

        CLIENT_CONNECTION_ACCEPTED_RESPONSE(103),
        CLIENT_CONNECTION_REJECTED_RESPONSE(104),


        /****************************
            Level 200: Data Transfer
         ***************************/

        CLIENT_GENERATED_MESSAGE(201),
        CLIENT_GENERATED_COMMAND(202),
        SERVER_GENERATED_MESSAGE(203),



        /****************************
         Level 300: User Account Actions
         ***************************/

        USER_ACCOUNT_REGISTRATION_REQUEST(300),
        USER_ACCOUNT_REGISTRATION_SUCCESSFUL(301),
        USER_ACCOUNT_REGISTRATION_FAILED(302),
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

        public static PacketCode get(int code)
        {
            for(PacketCode packetCode : PacketCode.values())
            {
                if(packetCode.code == code) return packetCode;
            }
            return null;
        }
    }
}
