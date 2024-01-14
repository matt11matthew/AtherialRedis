package net.atherial.api.redis.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Matthew Eisenberg on 6/2/2018 at 7:42 PM for the project CrusadeCommons
 */
public abstract class ConcurrentCache<K,O> extends Cache<K,O> {
    public ConcurrentCache() {
        super(new ConcurrentHashMap<>());
    }
}
