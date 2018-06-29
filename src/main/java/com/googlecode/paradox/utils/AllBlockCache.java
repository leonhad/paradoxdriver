/*
 * AllBlockCache.java 12/22/2014 Copyright (C) 2014 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All block cached.
 *
 * @author Andre Mikhaylov
 * @version 1.0
 * @since 1.2
 */
public final class AllBlockCache implements IBlockCache {
    
    /**
     * The cache instance.
     */
    private final Map<BlockOffset, ClobBlock> cache;
    
    /**
     * Create a new cache.
     */
    public AllBlockCache() {
        this.cache = new ConcurrentHashMap<>();
    }
    
    /**
     * Adds a list block to the cache.
     *
     * @param blocks
     *            the block list.
     */
    @Override
    public void add(final List<ClobBlock> blocks) {
        for (final ClobBlock block : blocks) {
            cache.put(block.getOffset(), block);
        }
    }
    
    /**
     * Clears the cache.
     */
    @Override
    public void close() {
        this.cache.clear();
    }
    
    /**
     * Gets a block by id.
     *
     * @param num
     *            the cache number.
     * @param offset
     *            the block offset.
     */
    @Override
    public ClobBlock get(final BlockOffset offset) {
        return cache.get(offset);
    }
}
