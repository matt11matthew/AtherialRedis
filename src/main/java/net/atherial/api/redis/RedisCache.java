package net.atherial.api.redis;

import net.atherial.api.redis.cache.ConcurrentCache;
import redis.clients.jedis.Jedis;

import java.util.Map;


public abstract class RedisCache<O> extends ConcurrentCache<String, O> {
    private String prefix;
    private AtherialRedis atherialRedis;

    public RedisCache(String prefix, AtherialRedis atherialRedis) {
        this.prefix = prefix;
        this.atherialRedis = atherialRedis;
    }


    public abstract O fromString(String input);

    public abstract String getObjectToString(O input);


    @Override
    public void add(String key, O o) {
        Jedis jedis = this.atherialRedis.getJedisPool().getResource();
        jedis.hset("atherial_caches:" + prefix, key, getObjectToString(o));
    }

    @Override
    public O get(String key) {
        Jedis jedis = this.atherialRedis.getJedisPool().getResource();
        Map<String, String> properties = jedis.hgetAll("atherial_caches:" + prefix);
        return fromString(properties.get(key));
    }


    @Override
    public void remove(String key) {
        Jedis jedis = this.atherialRedis.getJedisPool().getResource();
        jedis.hdel("atherial_caches:" + prefix, key);
    }

}
