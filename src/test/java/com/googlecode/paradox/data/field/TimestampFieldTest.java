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

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Unit test for {@link TimestampField} class.
 *
 * @version 1.2
 * @since 1.3
 */
public class TimestampFieldTest {

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
        final TimestampField field = new TimestampField();
        Assert.assertFalse("Invalid field value.", field.match(ParadoxType.NULL));
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final TimestampField field = new TimestampField();
        Assert.assertTrue("Invalid field type.", field.match(ParadoxType.TIMESTAMP));
    }

    /**
     * Test for timestamp reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testReadTimestamp() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT \"Timestamp\" FROM fields.timestamp")) {
            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertNull("Invalid value.", rs.getTimestamp("Timestamp"));

            Assert.assertTrue("Invalid Result Set state.", rs.next());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Assert.assertEquals("Invalid value.", "2020-02-01 01:00:01", format.format(rs.getTimestamp("Timestamp")));

            Assert.assertFalse("Invalid Result Set state.", rs.next());
        }
    }
}
