package net.atherial.api.redis;


@FunctionalInterface
public interface GenericAtherialPacketListener {

    AtherialRedisPacket onPacketReceived(String packetSender, AtherialRedisPacket packetContents);
}
