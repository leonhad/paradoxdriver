package com.googlecode.paradox.data.table.value;

import java.nio.charset.Charset;

import com.googlecode.paradox.metadata.BlobTable;

/**
 * Created by Andre on 22.12.2014.
 */
public class ClobDescriptor extends BlobDescriptor {

	private String leader;
	private Charset charset;

	public ClobDescriptor(final BlobTable file) {
		super(file);
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(final String leader) {
		this.leader = leader;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(final Charset charset) {
		this.charset = charset;
	}
}
