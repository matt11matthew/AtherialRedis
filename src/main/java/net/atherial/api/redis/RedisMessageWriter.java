package net.atherial.api.redis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisMessageWriter {

    private final String serverName;
    private final Gson gson = new Gson();
    private final JedisPool jedisPool;

    public RedisMessageWriter(String serverName, JedisPool jedisPool) {
        this.serverName = serverName;
        this.jedisPool = jedisPool;
    }

    public void sendPacket(Object message, String receivingServer) {
        JsonObject object = new JsonObject();
        object.addProperty("packetName", message.getClass().getName());
        object.addProperty("packetSender", serverName);
        object.addProperty("packetReceiver", receivingServer);
        object.add("packetContent", gson.toJsonTree(message));

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(AtherialRedis.getGlobalChannel(), object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
