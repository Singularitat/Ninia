package Ninia.utils.impl;

import java.util.HashMap;
import java.util.Map;

public class FixedSizeCache<K, V>
{
    private final Map<K, V> map;
    private final K[] keys;
    private int currIndex = 0;

    @SuppressWarnings("unchecked")
    public FixedSizeCache(int size)
    {
        this.map = new HashMap<>();
        if(size < 1)
            throw new IllegalArgumentException("Cache size must be at least 1!");
        this.keys = (K[]) new Object[size];
    }

    public void add(K key, V value)
    {
        if(keys[currIndex] != null)
        {
            map.remove(keys[currIndex]);
        }
        map.put(key, value);
        keys[currIndex] = key;
        currIndex = (currIndex + 1) % keys.length;
    }

    public boolean contains(K key)
    {
        return map.containsKey(key);
    }

    public V get(K key)
    {
        return map.get(key);
    }
}
