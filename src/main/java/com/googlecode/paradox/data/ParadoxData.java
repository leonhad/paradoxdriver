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

import com.googlecode.paradox.metadata.paradox.ParadoxDataFile;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles the paradox files (structure).
 *
 * @version 1.2
 * @since 1.4.0
 */
@SuppressWarnings({"i18n-java:V1008", "java:S109", "i18n-java:V1004"})
public class ParadoxData {

    /**
     * Minimum paradox file version.
     */
    protected static final int MINIMUM_VERSION = 4;
    private static final int CHARSET_DEFAULT = 437;
    private static final Charset CP437 = Charset.forName("cp437");
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
        CHARSET_TABLE.put(0x4e3, Charset.forName("cp1251"));
        CHARSET_TABLE.put(0x4e4, Charset.forName("cp1252"));
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
        if (dataFile.getVersionId() > ParadoxData.MINIMUM_VERSION) {
            // Set the charset.
            buffer.position(0x6A);
            int cp = buffer.getShort();

            // Force charset if have one.
            if (dataFile.getConnectionInfo().getCharset() != null) {
                dataFile.setCharset(dataFile.getConnectionInfo().getCharset());
            } else {
                dataFile.setCharset(CHARSET_TABLE.getOrDefault(cp, CP437));
                if (CHARSET_TABLE.get(cp) == null) {
                    Logger.getLogger(ParadoxData.class.getName()).finest(() -> "Charset " + cp + " not found.");
                }
            }
            buffer.position(0x78);
        } else {
            buffer.position(0x58);

            if (dataFile.getConnectionInfo().getCharset() != null) {
                dataFile.setCharset(dataFile.getConnectionInfo().getCharset());
            } else {
                dataFile.setCharset(CHARSET_TABLE.get(CHARSET_DEFAULT));
            }
        }
    }
}
