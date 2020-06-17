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

import static com.googlecode.paradox.utils.Utils.position;

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
    protected static final int MINIMIUM_VERSION = 4;

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
            position(buffer, 0x6A);
            int cp = buffer.getShort();
            // 437 is actually interpreted as cp1252.
            if (cp == 0x1B5) {
                cp = 0x4E4;
            }
            index.setCharset(Charset.forName("cp" + cp));

            position(buffer, 0x78);
        } else {
            position(buffer, 0x58);
        }
    }
}
