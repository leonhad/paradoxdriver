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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.table.value.BlobDescriptor;
import com.googlecode.paradox.metadata.ParadoxTable;
import java.sql.Types;

/**
 * Parses blob fields.
 *
 * @author Leonardo Alves da Costa
 * @author Michael Berry
 * @version 1.0
 * @since 1.3
 */
public final class BlobField extends AbstractLobField {
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == 0xD;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public BlobDescriptor getDescriptor(final ParadoxTable table) {
        return new BlobDescriptor(table.getBlobTable());
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public int getFieldType() {
        return Types.BLOB;
    }
    
}
