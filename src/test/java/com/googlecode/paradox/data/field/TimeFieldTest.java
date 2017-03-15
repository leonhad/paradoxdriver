/*
 * TimeFieldTest.java 07/07/2016 Copyright (C) 2016 Leonardo Alves da Costa This program is free software: you can
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
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link TimeField} class.
 *
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class TimeFieldTest {
    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final TimeField field = new TimeField();
        Assert.assertFalse(field.match(0));
    }

    /**
     * Test for parse method.
     *
     * @throws SQLException
     *             in case of parse errors.
     */
    @Test
    public void testParse() throws SQLException {
        final Calendar calendar = new GregorianCalendar(1, 0, 0);
        calendar.add(Calendar.MILLISECOND, 30000);
        final Time time = new Time(calendar.getTimeInMillis());

        final TimeField field = new TimeField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 0, 0, 0x75, 0x30 });
        final FieldValue value = field.parse(null, buffer, null);
        Assert.assertEquals(time, value.getTime());
    }

    /**
     * Test for null time.
     *
     * @throws SQLException
     *             in case of parse errors.
     */
    @Test
    public void testParseNull() throws SQLException {
        final TimeField field = new TimeField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 0, 0, 0, 0 });
        final FieldValue value = field.parse(null, buffer, null);
        Assert.assertNull(value.getTime());
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final TimeField field = new TimeField();
        Assert.assertTrue(field.match(0x14));
    }
}
