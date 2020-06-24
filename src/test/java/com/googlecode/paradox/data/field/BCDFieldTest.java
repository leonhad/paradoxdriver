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

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BCDFieldTest {

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
     * Test for BLOB reading.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testReadBlob() throws SQLException {
        try (Statement stmt = this.conn.createStatement(); ResultSet rs = stmt.executeQuery(
                "SELECT * FROM fields.bcd")) {
            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", 1.23D, rs.getDouble("A"), 0.001D);
            Assert.assertEquals("Invalid value.", 1.0D, rs.getDouble("B"), 0.001D);
            Assert.assertEquals("Invalid value.", 0.123D, rs.getDouble("C"), 0.001D);

            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", -1.23D, rs.getDouble("A"), 0.001D);
            Assert.assertEquals("Invalid value.", -1.0D, rs.getDouble("B"), 0.001D);
            Assert.assertEquals("Invalid value.", -0.123D, rs.getDouble("C"), 0.001D);

            Assert.assertTrue("Invalid Result Set state.", rs.next());
            Assert.assertEquals("Invalid value.", 0.0D, rs.getDouble("A"), 0.001D);
            Assert.assertNull("Invalid value.", rs.getObject("B"));
            Assert.assertEquals("Invalid value.", 0.9999D, rs.getDouble("C"), 0.001D);

            Assert.assertFalse("Invalid Result Set state.", rs.next());
        }
    }
}
