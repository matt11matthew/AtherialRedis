package net.atherial.api.redis.cache;

import java.util.HashMap;

/**
 * Created by Matthew Eisenberg on 6/2/2018 at 7:42 PM for the project CrusadeCommons
 */
public abstract class SyncCache<K,O> extends Cache<K,O> {
    public SyncCache() {
        super(new HashMap<>());
    }
}
