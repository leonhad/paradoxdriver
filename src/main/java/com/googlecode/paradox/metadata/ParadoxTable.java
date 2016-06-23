package com.googlecode.paradox.metadata;

import static java.nio.charset.Charset.forName;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a table data file.
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.2
 */
public class ParadoxTable extends ParadoxDataFile {

    private BlobTable blobFile = null;

    /**
     * Table charset.
     */
    private Charset charset = forName("Cp437");

    /**
     * Fields order in file.
     */
    private List<Short> fieldsOrder;

    /**
     * Creates a new instance.
     *
     * @param file table references file.
     * @param name table name.
     */
    public ParadoxTable(final File file, final String name) {
        super(file, name);
    }

    public BlobTable getBlobTable() {
        if (blobFile == null) {
            blobFile = new BlobTable(getFile(), getName());
        }
        return blobFile;
    }

    /**
     * Return the block size in bytes.
     *
     * @return the block size in bytes.
     */
    public int getBlockSizeBytes() {
        // The blockSize is always in KiB
        return blockSize * 1024;
    }

    /**
     * @return the charset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @return the fieldsOrder.
     */
    public List<Short> getFieldsOrder() {
        return fieldsOrder;
    }

    public ArrayList<ParadoxField> getPrimaryKeys() {
        final ArrayList<ParadoxField> ret = new ArrayList<ParadoxField>();
        for (int loop = 0; loop < primaryFieldCount; loop++) {
            ret.add(fields.get(loop));
        }
        return ret;
    }

    /**
     * If this table is valid.
     *
     * @return true if this table is valid.
     */
    @Override
    public boolean isValid() {
        return type == 0 || type == 2;
    }

    /**
     * Sets the charset.
     *
     * @param charset
     *            the charset to set.
     */
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    /**
     * Sets the file order.
     *
     * @param fieldsOrder
     *            the fieldsOrder to set.
     */
    public void setFieldsOrder(final List<Short> fieldsOrder) {
        this.fieldsOrder = fieldsOrder;
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
}
