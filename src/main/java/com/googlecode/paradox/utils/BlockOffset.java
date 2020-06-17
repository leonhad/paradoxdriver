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

/**
 *
 * @author Michael
 */
public class BlockOffset {
    
    private final long mainBlockOffset;
    private final int subBlockOffset;

    public BlockOffset(long mainBlockOffset, int subBlockOffset) {
        this.mainBlockOffset = mainBlockOffset;
        if (subBlockOffset > 63) {
            subBlockOffset = 0;
        }
        this.subBlockOffset = subBlockOffset;
    }
    
    public static BlockOffset fromRawLong(long pOffset) {
        return new BlockOffset( (int) (pOffset & 0xFFFFFF00), (int) (pOffset & 0xFF));
    }

    public long getMainBlockOffset() {
        return mainBlockOffset;
    }

    public int getSubBlockOffset() {
        return subBlockOffset;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (this.mainBlockOffset ^ (this.mainBlockOffset >>> 32));
        hash = 67 * hash + this.subBlockOffset;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockOffset other = (BlockOffset) obj;
        if (this.mainBlockOffset != other.mainBlockOffset) {
            return false;
        }
        return this.subBlockOffset == other.subBlockOffset;
    }

    @Override
    public String toString() {
        return mainBlockOffset + "," + subBlockOffset;
    }
    
}
