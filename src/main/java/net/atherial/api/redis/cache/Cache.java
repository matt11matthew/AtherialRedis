package net.atherial.api.redis.cache;

import java.util.Map;

/**
 * Created by Matthew Eisenberg on 6/2/2018 at 7:41 PM for the project CrusadeCommons
 */
public abstract class Cache<K, O> {
    protected Map<K, O> map;

    public Cache(Map<K, O> map) {
        this.map = map;
    }

    public void add(K k, O o) {
        map.put(k, o);
    }


    public <V> V get(Class<V> clazz) {
        return (V) map.values().stream().filter(o -> o.getClass().getName().equals(clazz.getName())).findFirst().orElseGet(null);
    }

    public O get(K k) {
        return map.get(k);
    }

    public void remove(K k) {
        map.remove(k);
    }

    public Map<K, O> getMap() {
        return map;
    }
}
