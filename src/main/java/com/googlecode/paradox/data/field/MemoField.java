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
import com.googlecode.paradox.planner.FieldValueUtils;
import com.googlecode.paradox.results.ParadoxFieldType;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * Parses memo fields.
 *
 * @version 1.3
 * @since 1.3
 */
public final class MemoField extends AbstractLobField {

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean match(final int type) {
        return type == ParadoxFieldType.MEMO.getType()
                || type == ParadoxFieldType.FORMATTED_MEMO.getType();
    }

    @Override
    protected Object getValue(final ParadoxTable table, final ByteBuffer value) throws ParadoxDataException {
        final byte[] bytes = new byte[value.remaining()];
        value.get(bytes);
        return FieldValueUtils.convert(bytes, table.getCharset());
    }
}
