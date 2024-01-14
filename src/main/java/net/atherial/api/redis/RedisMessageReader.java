package net.atherial.api.redis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.atherial.api.redis.utilities.JsonUtils;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

public class RedisMessageReader extends JedisPubSub {

    private final String serverName;
    private AtherialRedis atherialRedis;
    private final Gson gson = new Gson();

    public RedisMessageReader(String serverName, AtherialRedis atherialRedis) {
        this.serverName = serverName;
        this.atherialRedis = atherialRedis;
    }

    @Override
    public void onMessage(String channel, String message) {

        if (!JsonUtils.isValidJson(message)) {
            return;
        }

        try {

            JsonObject packetContent = (JsonObject) JsonUtils.getJsonParser().parse(message);

            String packetName = packetContent.get("packetName").getAsString();
            String packetSender = packetContent.get("packetSender").getAsString();
            String packetReceiver = packetContent.get("packetReceiver").getAsString();
//            String packetClass = packetContent.get("packetClass").getAsString();

            if (!packetReceiver.equals("all") && packetSender.equals(serverName)) return;

            if (packetReceiver.equalsIgnoreCase("all") || packetReceiver.equalsIgnoreCase(serverName)) {
                Class<? extends AtherialRedisPacket> packetContentClass = (Class<? extends AtherialRedisPacket>) Class.forName(packetName);
                AtherialRedisPacket finalPacketContent = gson.fromJson(packetContent.getAsJsonObject("packetContent"), packetContentClass);

                if (this.atherialRedis.getPacketListener() != null) {
                    finalPacketContent = this.atherialRedis.getPacketListener().onPacketReceived(packetSender, finalPacketContent);
                }
                List<AtherialPacketListener> listenerList = AtherialRedis.listeners.get(packetContentClass);
                if (listenerList != null) {
                    AtherialRedisPacket finalPacketContent1 = finalPacketContent;
                    listenerList.forEach(listener -> listener.onPacketReceived(packetSender, finalPacketContent1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
