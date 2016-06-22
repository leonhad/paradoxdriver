package com.googlecode.paradox.utils;

/**
 * Created by Andre on 25.12.2014.
 */
public class ClobBlock {

	private byte[] value;
	private final int num;
	private final short type;
	private final short offset;

	public ClobBlock(final int num, final short type, final short offset) {
		value = null;
		this.num = num;
		this.type = type;
		this.offset = offset;
	}

	public ClobBlock(final int num, final short type, final short offset, final byte[] value) {
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
