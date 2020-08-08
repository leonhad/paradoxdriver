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
package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.utils.TestUtil;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Unit test for {@link TableData}.
 *
 * @version 1.6
 * @since 1.0
 */
public class TableDataTest {

    /**
     * Connection string used in tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    public TableDataTest() {
        super();
    }

    /**
     * Register the driver.
     */
    @BeforeClass
    public static void initClass() {
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
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for invalid table
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInvalidTable() throws SQLException {
        Assert.assertEquals("Failed in count invalid tables.", 0,
                this.conn.getConnectionInfo().getCurrentSchema().list(this.conn.getConnectionInfo(), "not found").size());
    }

    /**
     * Test for table area codes.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadAreaCodes() throws SQLException {

        final List<Table> tables = this.conn.getConnectionInfo().getCurrentSchema()
                .list(this.conn.getConnectionInfo(), "areacodes");
        Assert.assertNotNull("List tables is null", tables);
        Assert.assertFalse("List tables is empty", tables.isEmpty());
        final Table table = tables.get(0);
        final List<Object[]> data = table.load(table.getFields());
        Assert.assertEquals("Error in load areacodes.db table.", table.getRowCount(), data.size());
    }

    /**
     * Test for contact table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadContacts() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "contacts");
        final Field[] fields = new Field[]{table.getFields()[0]};
        Assert.assertNotNull("Error loading contacts.db table data.", table.load(fields));
    }

    /**
     * Test for customer table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadCustomer() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "customer");
        final Field[] fields = new Field[]{table.getFields()[0]};
        Assert.assertNotNull("Error loading customer.db table data.", table.load(fields));
    }

    /**
     * Test for Hercules table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadHercules() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "hercules");
        Assert.assertNotNull("Error loading hercules.db table data.", table.load(table.getFields()));
    }

    /**
     * Test for orders table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadOrders() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "orders");
        final Field[] fields = new Field[]{table.getFields()[0]};
        Assert.assertNotNull("Error loading table data.", table.load(fields));
    }

    /**
     * Test for server table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadServer() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "server");
        final Field[] fields = new Field[]{table.getFields()[0]};
        Assert.assertNotNull("Error loading table data.", table.load(fields));
    }

    /**
     * Test for class sanity.
     */
    @Test
    public void testSanity() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(TableData.class));
    }
}
