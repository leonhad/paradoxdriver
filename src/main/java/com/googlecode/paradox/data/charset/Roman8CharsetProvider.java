/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.charset;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The HP Roman 8 charset provider.
 */
public class Roman8CharsetProvider extends CharsetProvider {

    private static final Charset ROMAN_8 = new Roman8Charset();
    private static final List<Charset> ROMAN8_LIST = Collections.singletonList(ROMAN_8);

    /**
     * Creates a new instance.
     */
    public Roman8CharsetProvider() {
        super();
    }

    /**
     * Gets the HP Roman 8 charset.
     *
     * @return the HP Roman 8 charset.
     */
    public static Charset roman8() {
        return ROMAN_8;
    }

    @Override
    public Iterator<Charset> charsets() {
        return ROMAN8_LIST.iterator();
    }

    @Override
    public Charset charsetForName(String charsetName) {
        if (charsetName.equals(ROMAN_8.name()) || ROMAN_8.aliases().contains(charsetName)) {
            return ROMAN_8;
        }

        return null;
    }
}
