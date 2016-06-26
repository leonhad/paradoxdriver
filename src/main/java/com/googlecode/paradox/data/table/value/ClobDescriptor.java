/*
 * ClobDescriptor.java
 *
 * 12/22/2014
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.table.value;

import java.nio.charset.Charset;

import com.googlecode.paradox.metadata.BlobTable;

/**
 * Describe a CLOB file.
 *
 * @author Leonardo Alves da Costa
 * @author Andre Mikhaylov
 * @since 1.2
 * @version 1.0
 */
public class ClobDescriptor extends BlobDescriptor {

    private Charset charset;
    private String leader;

    public ClobDescriptor(final BlobTable file) {
        super(file);
    }

    public Charset getCharset() {
        return charset;
    }

    public String getLeader() {
        return leader;
    }

    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

    public void setLeader(final String leader) {
        this.leader = leader;
    }
}
