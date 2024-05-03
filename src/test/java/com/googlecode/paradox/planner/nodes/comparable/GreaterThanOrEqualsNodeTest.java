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
package com.googlecode.paradox.planner.nodes.comparable;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.planner.nodes.FieldNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link GreaterThanOrEqualsNode} class.
 *
 * @since 1.6.0
 */
class GreaterThanOrEqualsNodeTest {
    /**
     * The connection string used in  tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeAll
    static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    void testToString() {
        final FieldNode first = new FieldNode("table", "first", null);
        final FieldNode last = new FieldNode("table", "last", null);
        final GreaterThanOrEqualsNode node = new GreaterThanOrEqualsNode(first, last, null);
        assertEquals("table.first >= table.last", node.toString());
    }

    /**
     * Test for {@link ResultSet} execution.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testResultSet() throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT B FROM fields.bcd where B >= -1")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("B"));
            assertTrue(rs.next());
            assertEquals(-1, rs.getInt("B"));
            assertFalse(rs.next());
        }
    }
}
