package com.googlecode.paradox.data.table.value;

import com.googlecode.paradox.metadata.BlobTable;

import java.io.File;

/**
 * Describe blob information from db file.
 *
 * Created by Andre on 22.12.2014.
 */
public class BlobDescriptor {

    private long length = 0;
    private long offset = 0;
    private short modificator = 0;
    private BlobTable file = null;

    public BlobDescriptor(BlobTable file) {
        setFile(file);
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public short getModificator() {
        return modificator;
    }

    public void setModificator(short modificator) {
        this.modificator = modificator;
    }

    public BlobTable getFile() {
        return file;
    }

    public void setFile(BlobTable file) {
        this.file = file;
    }

}
