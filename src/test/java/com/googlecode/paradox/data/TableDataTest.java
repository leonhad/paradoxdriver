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
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.utils.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link TableData}.
 *
 * @since 1.0
 */
class TableDataTest {

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
    @BeforeAll
    static void initClass() {
        new Driver();
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @AfterEach
    void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @BeforeEach
    void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for invalid table
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testInvalidTable() throws SQLException {
        assertEquals(0, this.conn.getConnectionInfo().getCurrentSchema().list(this.conn.getConnectionInfo(), "not found").size());
    }

    /**
     * Test for table area codes.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLoadAreaCodes() throws SQLException {

        final List<Table> tables = this.conn.getConnectionInfo().getCurrentSchema()
                .list(this.conn.getConnectionInfo(), "areacodes");
        assertNotNull(tables);
        assertFalse(tables.isEmpty());
        final Table table = tables.get(0);
        final List<Object[]> data = table.load(table.getFields());
        assertEquals(table.getRowCount(), data.size());
    }

    /**
     * Test for contact table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLoadContacts() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "contacts");
        final Field[] fields = new Field[]{table.getFields()[0]};
        assertNotNull(table.load(fields));
    }

    /**
     * Test for customer table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLoadCustomer() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "customer");
        final Field[] fields = new Field[]{table.getFields()[0]};
        assertNotNull(table.load(fields));
    }

    /**
     * Test for Hercules table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLoadHercules() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "hercules");
        assertNotNull(table.load(table.getFields()));
    }

    /**
     * Test for orders table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLoadOrders() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "orders");
        final Field[] fields = new Field[]{table.getFields()[0]};
        assertNotNull(table.load(fields));
    }

    /**
     * Test for server table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLoadServer() throws SQLException {
        final Table table = this.conn.getConnectionInfo().getCurrentSchema()
                .findTable(this.conn.getConnectionInfo(), "server");
        final Field[] fields = new Field[]{table.getFields()[0]};
        assertNotNull(table.load(fields));
    }

    /**
     * Test for class sanity.
     */
    @Test
    void testSanity() {
        assertTrue(TestUtil.assertSanity(TableData.class));
    }
}
