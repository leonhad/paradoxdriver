/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import java.util.List;

/**
 * Used to store clob block caches.
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @version 1.1
 * @since 1.2
 */
public interface IBlockCache {

    /**
     * Adds a block to the cache.
     *
     * @param blocks the blocks to add.
     */
    void add(List<ClobBlock> blocks);

    /**
     * Closes the cache.
     */
    void close();

    /**
     * Gets the CLOB block in cache.
     *
     * @param offset the CLOB offset.
     * @return the block in cache.
     */
    ClobBlock get(BlockOffset offset);
}
