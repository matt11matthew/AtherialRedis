package net.atherial.api.redis;


@FunctionalInterface
public interface AtherialPacketListener<T extends AtherialRedisPacket> {

    void onPacketReceived(String packetSender, T packetContents);
}
