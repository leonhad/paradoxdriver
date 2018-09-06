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
