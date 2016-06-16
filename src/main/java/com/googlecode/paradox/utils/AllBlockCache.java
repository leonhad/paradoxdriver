package com.googlecode.paradox.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All block cached.
 *
 * Created by Andre on 25.12.2014.
 */
public class AllBlockCache implements IBlockCache {

    private Map<Integer, Map<Short, ClobBlock>> cache;

    public AllBlockCache() {
        cache = new ConcurrentHashMap<Integer, Map<Short, ClobBlock>>();
    }

    public ClobBlock get(int num, short offset) {
        Map<Short, ClobBlock> map = cache.get(num);
        if (map != null) {
            return map.get(offset);
        }
        return null;
    }

    public void close() {
        for (Map<Short, ClobBlock> map: cache.values()) {
            map.clear();
        }
        cache.clear();
    }

    public void add(List<ClobBlock> blocks) {
        Map<Short, ClobBlock> map;
        for (ClobBlock block: blocks) {
            if (!cache.containsKey(block.getNum())) {
                map = new ConcurrentHashMap<Short, ClobBlock>();
                cache.put(block.getNum(), map);
            } else {
                map = cache.get(block.getNum());
            }
            map.put(block.getOffset(), block);
        }
    }
}
