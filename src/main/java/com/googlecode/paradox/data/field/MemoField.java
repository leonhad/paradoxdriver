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

import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;
import com.googlecode.paradox.rowset.ValuesConverter;

import java.nio.ByteBuffer;

/**
 * Parses memo fields.
 *
 * @version 1.4
 * @since 1.3
 */
public final class MemoField extends AbstractLobField {

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final ParadoxType type) {
        return type == ParadoxType.MEMO || type == ParadoxType.FORMATTED_MEMO;
    }

    @Override
    protected Object getValue(final ParadoxTable table, final ByteBuffer value) throws ParadoxDataException {
        final byte[] bytes = new byte[value.remaining()];
        value.get(bytes);
        return ValuesConverter.convert(bytes, table.getCharset());
    }
}
