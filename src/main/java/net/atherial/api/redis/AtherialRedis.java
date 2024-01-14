package net.atherial.api.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AtherialRedis {

    private static final String globalChannel;
    private GenericAtherialPacketListener packetListener;

    protected static ConcurrentHashMap<Class<? extends AtherialRedisPacket>, List<AtherialPacketListener>> listeners;

    static {
        globalChannel = "atherial";
        listeners = new ConcurrentHashMap<>();
    }

    private String hostName;
    private int port;
    private String password;

    private JedisPool jedisPool;
    private RedisMessageReader reader;
    private RedisMessageWriter writer;
    private String url;

    public AtherialRedis() {
    }
    public AtherialRedis(String url) {
        this.url = url;
    }

    public AtherialRedis(String hostName, int port, String password) {
        this.hostName = hostName;
        this.port = port;
        this.password = password;
    }



    public void connect(String serverName) {

        if (url==null) {
            jedisPool = new JedisPool(new JedisPoolConfig(), (hostName == null ? "localhost" : hostName), port, 0, (password.length() < 1 ? null : password));
        } else {
            this.jedisPool = new JedisPool(new JedisPoolConfig(),url);
        }

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
        } catch (Exception e) {
            e.printStackTrace();
        }

        reader = new RedisMessageReader(serverName, this);
        writer = new RedisMessageWriter(serverName, jedisPool);

        new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(reader, getGlobalChannel());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendPacket(Object packetContents, String receiver) {
        writer.sendPacket(packetContents, receiver);
    }

    public final void disable() {
        if (reader.isSubscribed())
            reader.unsubscribe();
        jedisPool.destroy();
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public final <T extends AtherialRedisPacket> void registerListener(Class<T> msg, AtherialPacketListener<T> listener) {

        if (listeners.containsKey(msg)) {
            listeners.get(msg).add(listener);
            return;
        }

        List<AtherialPacketListener> list = new ArrayList<>();
        list.add(listener);
        listeners.put(msg, list);
    }

    public static String getGlobalChannel() {
        return globalChannel;
    }

    public GenericAtherialPacketListener getPacketListener() {
        return packetListener;
    }

    public void setPacketListener(GenericAtherialPacketListener packetListener) {
        this.packetListener = packetListener;
    }
}
