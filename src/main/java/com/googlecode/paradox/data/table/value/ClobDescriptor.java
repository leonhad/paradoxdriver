package com.googlecode.paradox.data.table.value;

import com.googlecode.paradox.metadata.BlobTable;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by Andre on 22.12.2014.
 */
public class ClobDescriptor extends BlobDescriptor {

    private String leader;
    private Charset charset;

    public ClobDescriptor(BlobTable file) {
        super(file);
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
