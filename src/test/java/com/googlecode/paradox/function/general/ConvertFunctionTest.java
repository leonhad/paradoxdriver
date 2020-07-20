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
package com.googlecode.paradox.function.general;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.string.RightFunction;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Unit test for {@link RightFunction}.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class ConvertFunctionTest {

    /**
     * The connection string used in this tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Creates a new instance.
     */
    public ConvertFunctionTest() {
        super();
    }

    /**
     * Register the database driver.
     */
    @BeforeClass
    public static void initClass() {
        new Driver();
    }

    /**
     * Close the test connection.
     *
     * @throws SQLException in case of failures.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws SQLException in case of failures.
     */
    @Before
    @SuppressWarnings("java:S2115")
    public void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for convert between types.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testRight() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select CONVERT('1234', INTEGER) ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", 1234, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for convert using charset.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharset() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select CONVERT(Note using cp1251) from db.NOTE1251 where Id = 2");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", "Удивительное устройство USB-флешки Kingston DataTraveler",
                    rs.getString(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for convert using charset with bytes.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharsetBytes() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select convert(BYTES using \"UTF-16LE\") from fields.bytes");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", "123", rs.getString(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}