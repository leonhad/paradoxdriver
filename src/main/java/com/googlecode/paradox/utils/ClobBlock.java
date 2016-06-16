package com.googlecode.paradox.utils;

/**
 * Created by Andre on 25.12.2014.
 */
public class ClobBlock {

    private byte[] value;
    private int num;
    private short type;
    private short offset;

    public ClobBlock(int num, short type, short offset) {
        value = null;
        this.num = num;
        this.type = type;
        this.offset = offset;
    }

    public ClobBlock(int num, short type, short offset, byte[] value) {
        this(num, type, offset);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    public int getNum() {
        return num;
    }

    public short getType() {
        return type;
    }

    public short getOffset() {
        return offset;
    }
}
