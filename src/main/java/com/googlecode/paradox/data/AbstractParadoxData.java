package com.googlecode.paradox.data;

import com.googlecode.paradox.metadata.ParadoxDataFile;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Handles the paradox files (structure).
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.4.0
 */
public abstract class AbstractParadoxData {

    /**
     * Minimum paradox file version.
     */
    private static final int MINIMIUM_VERSION = 4;

    /**
     * Creates a new instance.
     */
    protected AbstractParadoxData() {
        super();
    }

    /**
     * Parse and handle the version ID.
     *
     * @param buffer the buffer to parse.
     * @param index  the paradox index.
     */
    protected static void parseVersionID(final ByteBuffer buffer, final ParadoxDataFile index) {
        if (index.getVersionId() > AbstractParadoxData.MINIMIUM_VERSION) {
            // Set the charset.
            buffer.position(0x6A);
            int cp = buffer.getShort();
            // 437 is actually interpreted as cp1252.
            if (cp == 0x1B5) {
                cp = 0x4E4;
            }
            index.setCharset(Charset.forName("cp" + cp));

            buffer.position(0x78);
        } else {
            buffer.position(0x58);
        }
    }

}
