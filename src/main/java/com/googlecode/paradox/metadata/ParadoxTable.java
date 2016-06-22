package com.googlecode.paradox.metadata;

import static java.nio.charset.Charset.forName;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Load Table Data
 *
 * @author Leonardo Costa
 * @since 14/03/2009
 * @version 1.1
 */
public class ParadoxTable extends AbstractTable {

    private ArrayList<Short> fieldsOrder;
    private Charset charset = forName("Cp437");

    private BlobTable blobFile = null;

    public ParadoxTable(final File file, final String name) {
        super(file, name);
    }

    /**
     * Gets the table name.
     *
     * @return the table name.
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Check if the tables are equals based on table name.
     *
     * @param obj the table to compare.
     * @return true if the tables have the same name.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ParadoxTable) {
            return getName().equals(((AbstractTable) obj).getName());
        }
        return false;
    }

    /**
     * Gets the table name hash code.
     * @return the table name hash code.
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public ArrayList<ParadoxField> getPrimaryKeys() {
        final ArrayList<ParadoxField> ret = new ArrayList<ParadoxField>();
        for (int loop = 0; loop < primaryFieldCount; loop++) {
            ret.add(fields.get(loop));
        }
        return ret;
    }

    /**
     * Return the block size in bytes
     *
     * @return the block size in bytes
     */
    public int getBlockSizeBytes() {
        // The blockSize is always in KiB
        return blockSize * 1024;
    }

    /**
     * If this table is valid
     *
     * @return true if this table is valid
     */
    @Override
    public boolean isValid() {
        return type == 0 || type == 2;
    }

    /**
     * @return the fieldsOrder
     */
    public ArrayList<Short> getFieldsOrder() {
        return fieldsOrder;
    }

    /**
     * @param fieldsOrder the fieldsOrder to set
     */
    public void setFieldsOrder(ArrayList<Short> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
    }

    /**
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public BlobTable getBlobTable() {
        if (blobFile == null) {
            blobFile = new BlobTable(this.getFile(), getName());
        }
        return blobFile;
    }
}
