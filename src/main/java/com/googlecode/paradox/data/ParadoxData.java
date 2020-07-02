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
package com.googlecode.paradox.data;

import com.googlecode.paradox.metadata.ParadoxDataFile;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles the paradox files (structure).
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.4.0
 */
public class ParadoxData {

    private static final int CHARSET_DEFAULT = 437;
    private static final Charset CP437 = Charset.forName("cp437");

    /**
     * Minimum paradox file version.
     */
    protected static final int MINIMIUM_VERSION = 4;

    private static final Map<Integer, Charset> CHARSET_TABLE = new HashMap<>();

    static {
        CHARSET_TABLE.put(437, CP437);
        CHARSET_TABLE.put(850, Charset.forName("cp850"));
        CHARSET_TABLE.put(852, Charset.forName("cp852"));
        CHARSET_TABLE.put(861, Charset.forName("cp861"));
        CHARSET_TABLE.put(862, Charset.forName("cp862"));
        CHARSET_TABLE.put(863, Charset.forName("cp863"));
        CHARSET_TABLE.put(865, Charset.forName("cp865"));
        CHARSET_TABLE.put(866, Charset.forName("cp866"));
        CHARSET_TABLE.put(867, Charset.forName("cp862"));
        CHARSET_TABLE.put(932, Charset.forName("windows-31j"));
        CHARSET_TABLE.put(936, Charset.forName("cp936"));
        CHARSET_TABLE.put(1251, Charset.forName("windows-1251"));
        CHARSET_TABLE.put(1252, Charset.forName("cp1252"));
    }

    /**
     * Creates a new instance.
     */
    protected ParadoxData() {
        // Unused.
    }

    protected static void checkDBEncryption(final ByteBuffer buffer, final ParadoxDataFile dataFile, int blockSize,
                                            long blockNumber) {
        if (dataFile.isEncrypted()) {
            byte[] b = buffer.array();
            EncryptedData.decryptDBBlock(b, dataFile.getEncryptedData(), blockSize, blockNumber);
        }
    }

    /**
     * Parse and handle the version ID.
     *
     * @param buffer   the buffer to parse.
     * @param dataFile the paradox index.
     */
    protected static void parseVersionID(final ByteBuffer buffer, final ParadoxDataFile dataFile) {
        if (dataFile.getVersionId() > ParadoxData.MINIMIUM_VERSION) {
            // Set the charset.
            buffer.position(0x6A);
            int cp = buffer.getShort();

            // Force charset if have one.
            if (dataFile.getConnection().getCharset() != null) {
                dataFile.setCharset(dataFile.getConnection().getCharset());
            } else {
                dataFile.setCharset(CHARSET_TABLE.getOrDefault(cp, CP437));
                if (CHARSET_TABLE.get(cp) == null) {
                    Logger.getLogger(ParadoxData.class.getName()).severe(() -> "Charset " + cp + " not found.");
                }
            }
            buffer.position(0x78);
        } else {
            buffer.position(0x58);

            if (dataFile.getConnection().getCharset() != null) {
                dataFile.setCharset(dataFile.getConnection().getCharset());
            } else {
                dataFile.setCharset(CHARSET_TABLE.get(CHARSET_DEFAULT));
            }
        }
    }
}
