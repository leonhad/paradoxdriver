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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.data.IndexData;
import com.googlecode.paradox.data.PrimaryKeyData;
import com.googlecode.paradox.data.TableData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link Utils}.
 *
 * @since 1.2
 */
class UtilsTest {

    /**
     * Connection string used in tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * Database connection.
     */
    private Connection conn;

    /**
     * Register the driver.
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
        this.conn = DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test if the constructor is private.
     */
    @Test
    void testConstructorIsPrivate() {
        assertTrue(TestUtil.assertSanity(Utils.class));
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)} method
     * with invalid value.
     */
    @Test
    void testIsNotWrapFor() {
        assertFalse(Utils.isWrapperFor(this.conn, Connection.class));
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)}.
     */
    @Test
    void testIsWrapFor() {
        assertTrue(Utils.isWrapperFor(this.conn, ParadoxConnection.class));
    }

    /**
     * Test for unwrap.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testUnwrap() throws Exception {
        assertNotNull(Utils.unwrap(this.conn, ParadoxConnection.class));
    }

    /**
     * Test for a unwrap with wrong class.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testUnwrapImpossible() throws Exception {
        assertThrows(SQLException.class, () -> Utils.unwrap(this.conn, Integer.class));
    }

    /**
     * Test for a valid connection.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testValidConnection() throws SQLException {
        assertTrue(this.conn.isWrapperFor(ParadoxConnection.class));
        assertNotNull(this.conn.unwrap(ParadoxConnection.class));
    }

    /**
     * Test for remove suffix extension.
     */
    @Test
    void testRemoveSuffix() {
        assertEquals("FILE", Utils.removeSuffix("FILE.DB", "DB"));
        assertEquals("FILE", Utils.removeSuffix("FILE", "DB"));
        assertEquals("FILE.TXT", Utils.removeSuffix("FILE.TXT", "DB"));
        assertNull(Utils.removeSuffix(null, null));
    }

    /**
     * Test for utilities classes.
     */
    @Test
    void testClassesIntegrity() {
        assertTrue(TestUtil.assertSanity(Utils.class));
        assertTrue(TestUtil.assertSanity(DateUtils.class));
        assertTrue(TestUtil.assertSanity(TableData.class));
        assertTrue(TestUtil.assertSanity(Expressions.class));
        assertTrue(TestUtil.assertSanity(IndexData.class));
        assertTrue(TestUtil.assertSanity(PrimaryKeyData.class));
        assertTrue(TestUtil.assertSanity(Constants.class));
    }
}
