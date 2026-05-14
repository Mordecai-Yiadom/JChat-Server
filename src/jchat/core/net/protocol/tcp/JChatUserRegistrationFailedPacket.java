package jchat.core.net.protocol.tcp;

public class JChatUserRegistrationFailedPacket
{
    private static final String FAILURE_REASON_META_TAG = "FailureReason";

    public static JChatTCPPacket create(String failureReason)
    {
        return new JChatTCPPacket.Builder()
                .setPacketCode(JChatTCPPacket.PacketCode.USER_ACCOUNT_REGISTRATION_FAILED)
                .addMetaData(FAILURE_REASON_META_TAG, failureReason)
                .setPayload("Filler")
                .build();
    }

    public static String parseFailureReason(JChatTCPPacket packet)
    {
        return packet.getMetaData(FAILURE_REASON_META_TAG);
    }

}
