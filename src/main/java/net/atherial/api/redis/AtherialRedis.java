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
    private String serverName;

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


    public String getServerName() {
        return serverName;
    }

    public void connect(String serverName) {
        this.serverName = serverName;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        if (url==null) {
            jedisPool = new JedisPool(jedisPoolConfig, (hostName == null ? "localhost" : hostName), port, 0, (password.length() < 1 ? null : password));
        } else {
            this.jedisPool = new JedisPool(jedisPoolConfig,url);
        }

        try (Jedis jedis = jedisPool.getResource()) {
            String response = jedis.ping();
            if (!"PONG".equals(response)) {
                // Handle the scenario where Redis is not responding properly
                throw new RuntimeException("Failed to ping Redis server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Consider re-throwing, logging, or handling the exception as per your application needs
        }


        reader = new RedisMessageReader(serverName, this);
        writer = new RedisMessageWriter(serverName, jedisPool);

//        new Thread(() -> {
//            try (Jedis jedis = jedisPool.getResource()) {
//                jedis.subscribe(reader, getGlobalChannel());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(reader, getGlobalChannel());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
