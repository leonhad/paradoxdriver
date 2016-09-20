/*
 * ClobBlock.java
 *
 * 12/22/2014
 * Copyright (C) 2014 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

/**
 * Stores the CLOB block.
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @version 1.1
 * @since 1.2
 */
public class ClobBlock {

    /**
     * The CLOB number.
     */
    private final int num;

    /**
     * The CLOB offset.
     */
    private final short offset;

    /**
     * The CLOB type.
     */
    private final short type;

    /**
     * The CLOB data.
     */
    private final byte[] value;

    /**
     * Create a new instance.
     *
     * @param num
     *         the CLOB number.
     * @param type
     *         the CLOB type.
     * @param offset
     *         the CLOB offset.
     */
    public ClobBlock(final int num, final short type, final short offset) {
        this(num, type, offset, null);
    }

    /**
     * Create a new instance.
     *
     * @param num
     *         the CLOB number.
     * @param type
     *         the CLOB type.
     * @param offset
     *         the CLOB offset.
     * @param value
     *         the CLOB data.
     */
    public ClobBlock(final int num, final short type, final short offset, final byte[] value) {
        this.num = num;
        this.type = type;
        this.offset = offset;
        this.value = value;
    }

    /**
     * Gets the CLOB number.
     *
     * @return the CLOB number.
     */
    public int getNum() {
        return num;
    }

    /**
     * Gets the CLOB offset.
     *
     * @return the CLOB offset.
     */
    public short getOffset() {
        return offset;
    }

    /**
     * Gets the CLOB type.
     *
     * @return the CLOB type.
     */
    public short getType() {
        return type;
    }

    /**
     * Gets the CLOB data value.
     *
     * @return the CLOB data value.
     */
    public byte[] getValue() {
        return value;
    }
}
