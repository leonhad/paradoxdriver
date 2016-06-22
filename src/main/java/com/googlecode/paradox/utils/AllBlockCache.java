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

    private final Map<Integer, Map<Short, ClobBlock>> cache;

    public AllBlockCache() {
        cache = new ConcurrentHashMap<Integer, Map<Short, ClobBlock>>();
    }

    @Override
    public ClobBlock get(final int num, final short offset) {
        final Map<Short, ClobBlock> map = cache.get(num);
        if (map != null) {
            return map.get(offset);
        }
        return null;
    }

    @Override
    public void close() {
        for (final Map<Short, ClobBlock> map : cache.values()) {
            map.clear();
        }
        cache.clear();
    }

    @Override
    public void add(final List<ClobBlock> blocks) {
        Map<Short, ClobBlock> map;
        for (final ClobBlock block : blocks) {
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
