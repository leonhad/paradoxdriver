/*
 * BooleanFieldTest.java 07/07/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.data.table.value.FieldValue;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link BooleanField} class.
 *
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class BooleanFieldTest {
    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final BooleanField field = new BooleanField();
        Assert.assertFalse(field.match(0));
    }

    /**
     * Test for parse false value.
     *
     * @throws SQLException
     *             in case of parse errors.
     */
    @Test
    public void testParseFalse() throws SQLException {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] { -128 });
        final FieldValue value = field.parse(null, buffer, null);
        Assert.assertFalse(value.getBoolean());
    }

    /**
     * Test for parse invalid value.
     *
     * @throws SQLException
     *             in case of parse errors.
     */
    @Test(expected = SQLException.class)
    public void testParseInvalidValue() throws SQLException {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 1 });
        field.parse(null, buffer, null);
    }

    /**
     * Test for parse null value.
     *
     * @throws SQLException
     *             in case of parse errors.
     */
    @Test
    public void testParseNull() throws SQLException {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 0 });
        final FieldValue value = field.parse(null, buffer, null);
        Assert.assertNull(value.getBoolean());
    }

    /**
     * Test for parse true value.
     *
     * @throws SQLException
     *             in case of parse errors.
     */
    @Test
    public void testParseTrue() throws SQLException {
        final BooleanField field = new BooleanField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] { -127 });
        final FieldValue value = field.parse(null, buffer, null);
        Assert.assertTrue(value.getBoolean());
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final BooleanField field = new BooleanField();
        Assert.assertTrue(field.match(9));
    }
}
