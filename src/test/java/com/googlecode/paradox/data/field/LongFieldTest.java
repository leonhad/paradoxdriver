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

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.integration.MainTest;
import org.junit.*;

import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Unit test for {@link LongField} class.
 *
 * @author Leonardo Costa
 * @version 1.1
 * @since 1.3
 */
public class LongFieldTest {

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
        this.conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "fields");
    }

    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final LongField field = new LongField();
        Assert.assertFalse("Invalid field type.", field.match(0));
    }

    /**
     * Test for parse method.
     *
     * @throws SQLException in case of parse errors.
     */
    @Test
    public void testParse() throws SQLException {
        final LongField field = new LongField();

        // Test positive values
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00});
        FieldValue value = field.parse(null, buffer, null);
        Assert.assertEquals("Invalid number value.", 256L, value.getNumber());

        // Test negative values
        buffer = ByteBuffer.wrap(new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0x00});
        value = field.parse(null, buffer, null);
        Assert.assertEquals("Invalid number value.", -256L, value.getNumber());
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final LongField field = new LongField();
        Assert.assertTrue("Invalid field type.", field.match(4));
    }

    /**
     * Test for LONG reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testReadLong() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT \"Time\" FROM fields.time")) {
            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", "01:00:01", rs.getTime("Time").toString());

            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertNull("Invalid value.", rs.getTime("Time"));

            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", "03:00:03", rs.getTime("Time").toString());

            Assert.assertFalse("Invalid Result Set state.", rs.next());
        }
    }
}
