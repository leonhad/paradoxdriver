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

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Unit test for {@link VarcharField} class.
 *
 * @version 1.4
 * @since 1.3
 */
public class VarcharFieldTest {

    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final VarcharField field = new VarcharField();
        Assert.assertFalse("Invalid field value.", field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    public void testParse() {
        final ParadoxTable table = new ParadoxTable(null, null, null);
        table.setCharset(StandardCharsets.ISO_8859_1);
        final ParadoxField paradoxField = new ParadoxField(ParadoxType.VARCHAR);
        paradoxField.setSize("test".length());
        final VarcharField field = new VarcharField();
        final ByteBuffer buffer = ByteBuffer.wrap("test".getBytes(table.getCharset()));
        final Object value = field.parse(table, buffer, paradoxField);
        Assert.assertEquals("Value not equals.", "test", value);
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final VarcharField field = new VarcharField();
        Assert.assertTrue("Field doesn't match.", field.match(ParadoxType.VARCHAR));
    }
}
