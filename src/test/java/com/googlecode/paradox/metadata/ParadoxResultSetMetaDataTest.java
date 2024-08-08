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
package com.googlecode.paradox.metadata;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link ParadoxResultSetMetaData} class.
 *
 * @version 1.1
 * @since 1.3
 */
class ParadoxResultSetMetaDataTest {

    /**
     * The connection string used by tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws Exception in case of failures.
     */
    @BeforeAll
    static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @AfterEach
    void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @BeforeEach
    void connect() throws Exception {
        this.conn = (ParadoxConnection) DriverManager.getConnection(ParadoxResultSetMetaDataTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for instance.
     */
    @Test
    void testInstance() {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        assertEquals(0, metaData.getColumnCount());
    }

    /**
     * Test for invalid column with high value.
     */
    @Test
    void testInvalidColumnHighValue() {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        assertThrows(SQLException.class, () -> metaData.getColumnName(5));
    }

    /**
     * Test for invalid column with low value.
     */
    @Test
    void testInvalidColumnLowValue() {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        assertThrows(SQLException.class, () -> metaData.getColumnName(0));
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)}.
     */
    @Test
    void testIsWrapFor() {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        assertTrue(metaData.isWrapperFor(ParadoxResultSetMetaData.class));
    }

    /**
     * Test for unwrap.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testUnwrap() throws Exception {
        final ParadoxResultSetMetaData metaData = new ParadoxResultSetMetaData(this.conn.getConnectionInfo(), Collections.emptyList());
        assertNotNull(metaData.unwrap(ParadoxResultSetMetaData.class));
    }

    /**
     * Test for the precision and scale for decimals.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testPrecisionScaleDecimal() throws Exception {
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM db.DECIMAL")) {
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                assertEquals(15, metaData.getPrecision(1));
                assertEquals(6, metaData.getScale(1));
            } else {
                fail("No catalog selected.");
            }
        }
    }

    /**
     * Test for the precision and scale for BCD.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testPrecisionScaleBcd() throws Exception {
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM fields.bcd")) {
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                assertEquals(2, metaData.getPrecision(1));
                assertEquals(0, metaData.getScale(1));
                assertEquals(2, metaData.getColumnDisplaySize(1));
            } else {
                fail("No catalog selected.");
            }
        }
    }
}
