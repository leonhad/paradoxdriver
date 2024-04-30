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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.*;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Unit test for {@link TimeField} class.
 *
 * @version 1.3
 * @since 1.3
 */
public class TimeFieldTest {

    /**
     * Connection string used in tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Register the driver.
     */
    @BeforeClass
    public static void setUp() {
        new Driver();
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @Before
    public void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final TimeField field = new TimeField();
        Assert.assertFalse("Invalid field value.", field.match(ParadoxType.NULL));
    }

    /**
     * Test for parse method.
     */
    @Test
    public void testParse() {
        final Calendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1);
        calendar.add(Calendar.MILLISECOND, 30000);
        final Time time = new Time(calendar.getTimeInMillis());

        final TimeField field = new TimeField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0, 0, 0x75, 0x30});
        final Object value = field.parse(null, buffer, null);
        Assert.assertEquals("Invalid time value.", time, value);
    }

    /**
     * Test for null time.
     */
    @Test
    public void testParseNull() {
        final TimeField field = new TimeField();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0, 0, 0, 0});
        final Object value = field.parse(null, buffer, null);
        Assert.assertNull("Invalid time value.", value);
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final TimeField field = new TimeField();
        Assert.assertTrue("Invalid field type.", field.match(ParadoxType.TIME));
    }

    /**
     * Test for TIME reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testReadTime() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Id, LONG FROM fields.long")) {
            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", 1, rs.getInt("Id"));
            Assert.assertEquals("Invalid value.", 1, rs.getInt("LONG"));

            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", 2, rs.getInt("Id"));
            Assert.assertEquals("Invalid value.", 2, rs.getInt("LONG"));

            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", 3, rs.getInt("Id"));
            Assert.assertNull("Invalid value.", rs.getObject("LONG"));

            Assert.assertFalse("Invalid Result Set state.", rs.next());
        }
    }
}
