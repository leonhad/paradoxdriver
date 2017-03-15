/*
 * FieldValueTest.java 07/23/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.table.value;

import java.sql.SQLDataException;
import java.sql.Types;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link FieldValue} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class FieldValueTest {

    /**
     * Test for field value.
     *
     * @throws SQLDataException
     *             in case of errors.
     */
    @Test
    public void testFieldValue() throws SQLDataException {
        final FieldValue fieldValue = new FieldValue(Boolean.TRUE, Types.BOOLEAN);
        Assert.assertEquals("Invalid type.", Types.BOOLEAN, fieldValue.getType());
        Assert.assertTrue("Invalid value.", fieldValue.getBoolean());
        Assert.assertNull("Testing for invalid field.", fieldValue.getField());
    }

    /**
     * Test for invalid {@link Boolean} value.
     *
     * @throws SQLDataException
     *             in there are no errors.
     */
    @Test(expected = SQLDataException.class)
    public void testInvalidBoolean() throws SQLDataException {
        final FieldValue fieldValue = new FieldValue("test value", Types.VARCHAR);
        fieldValue.getBoolean();
    }

    /**
     * Test for invalid {@link java.sql.Date} value.
     *
     * @throws SQLDataException
     *             in there are no errors.
     */
    @Test(expected = SQLDataException.class)
    public void testInvalidDate() throws SQLDataException {
        final FieldValue fieldValue = new FieldValue("test value", Types.VARCHAR);
        fieldValue.getDate();
    }

    /**
     * Test for invalid {@link Number} value.
     *
     * @throws SQLDataException
     *             in there are no errors.
     */
    @Test(expected = SQLDataException.class)
    public void testInvalidNumber() throws SQLDataException {
        final FieldValue fieldValue = new FieldValue("test value", Types.VARCHAR);
        fieldValue.getNumber();
    }

    /**
     * Test for invalid {@link java.sql.Time} value.
     *
     * @throws SQLDataException
     *             in there are no errors.
     */
    @Test(expected = SQLDataException.class)
    public void testInvalidTime() throws SQLDataException {
        final FieldValue fieldValue = new FieldValue("test value", Types.VARCHAR);
        fieldValue.getTime();
    }

    /**
     * Test for not null value.
     *
     * @throws SQLDataException
     *             in case of errors.
     */
    @Test
    public void testNotNullValue() throws SQLDataException {
        final FieldValue fieldValue = new FieldValue(Boolean.FALSE, Types.BOOLEAN);
        Assert.assertFalse("Testing for invalid null value.", fieldValue.isNull());
    }

    /**
     * Test for null value.
     *
     * @throws SQLDataException
     *             in case of errors.
     */
    @Test
    public void testNullValue() throws SQLDataException {
        final FieldValue fieldValue = new FieldValue(null, Types.BOOLEAN);
        Assert.assertTrue("Testing for invalid null value.", fieldValue.isNull());
    }
}