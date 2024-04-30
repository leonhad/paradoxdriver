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
package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * Unit test for {@link IndexData}.
 *
 * @version 1.4
 * @since 1.0
 */
public class IndexDataTest {

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
     *
     * @throws ClassNotFoundException in case of connection errors.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Used to close the test connection.
     *
     * @throws Exception in case closing of errors.
     */
    @After
    public void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws Exception in case of connection errors.
     */
    @Before
    public void connect() throws Exception {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for index listing.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testListIndexes() throws Exception {
        Assert.assertEquals("Not empty index", 0, this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "contacts")
                .getIndexes().length);
    }

    /**
     * Test for index name.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testIndexName() throws Exception {
        try (final ResultSet rs = this.conn.getMetaData().getIndexInfo(null, "joins", "Indexed", false, true)) {
            Assert.assertTrue("Invalid ResultSet state", rs.next());
            Assert.assertEquals("Invalid index name", "Descending", rs.getString("INDEX_NAME"));
        }
    }
}
