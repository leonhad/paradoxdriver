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
import com.googlecode.paradox.data.table.value.FieldValue;
import com.googlecode.paradox.integration.MainTest;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.utils.TestUtil;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for {@link TableData}.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.0
 */
public class TableDataTest {

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
        this.conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for invalid table
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInvalidTable() throws SQLException {
        Assert.assertEquals("Failed in count invalid tables.", 0,
                TableData.listTables(this.conn.getCurrentSchema(), "not found.db", this.conn).size());
    }

    /**
     * Test for table area codes.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadAreaCodes() throws SQLException {
        final List<ParadoxTable> tables = TableData.listTables(this.conn.getCurrentSchema(), "areacodes.db", this.conn);
        Assert.assertNotNull("List tables is null", tables);
        Assert.assertFalse("List tables is empty", tables.isEmpty());
        final ParadoxTable table = tables.get(0);
        final List<List<FieldValue>> data = TableData.loadData(table, table.getFields());
        Assert.assertEquals("Error in load areacodes.db table.", table.getRowCount(), data.size());
    }

    /**
     * Test for contact table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadContacts() throws SQLException {
        final ParadoxTable table = TableData.listTables(this.conn.getCurrentSchema(), "contacts.db", this.conn).get(0);
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        fields.add(table.getFields().get(0));
        Assert.assertNotNull("Error loading contacts.db table data.", TableData.loadData(table, fields));
    }

    /**
     * Test for customer table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadCustomer() throws SQLException {
        final ParadoxTable table = TableData.listTables(this.conn.getCurrentSchema(), "customer.db", this.conn).get(0);
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        fields.add(table.getFields().get(0));
        Assert.assertNotNull("Error loading customer.db table data.", TableData.loadData(table, fields));
    }

    /**
     * Test for Hercules table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadHercules() throws SQLException {
        final ParadoxTable table = TableData.listTables(this.conn.getCurrentSchema(), "hercules.db", this.conn).get(0);
        Assert.assertNotNull("Error loading hercules.db table data.", TableData.loadData(table, table.getFields()));
    }

    /**
     * Test for orders table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadOrders() throws SQLException {
        final ParadoxTable table = TableData.listTables(this.conn.getCurrentSchema(), "orders.db", this.conn).get(0);
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        fields.add(table.getFields().get(0));
        Assert.assertNotNull("Error loading table data.", TableData.loadData(table, fields));
    }

    /**
     * Test for server table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLoadServer() throws SQLException {
        final ParadoxTable table = TableData.listTables(this.conn.getCurrentSchema(), "server.db", this.conn).get(0);
        final ArrayList<ParadoxField> fields = new ArrayList<>();
        fields.add(table.getFields().get(0));
        Assert.assertNotNull("Error loading table data.", TableData.loadData(table, fields));
    }

    /**
     * Test for class sanity.
     */
    @Test
    public void testSanity() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertUtilityClassWellDefined(TableData.class));
    }
}
