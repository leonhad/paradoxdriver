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
 * Unit test for {@link BetweenNode} class.
 *
 * @since 1.3
 */
class BetweenNodeTest {

    /**
     * The connection string used in this tests.
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
     * Test the field node.
     */
    @Test
    void testField() {
        final FieldNode field = new FieldNode("table", "field", null);
        field.setAlias("alias");
        final FieldNode first = new FieldNode("table", "first", null);
        final FieldNode last = new FieldNode("table", "last", null);
        final BetweenNode node = new BetweenNode(field, first, last, null);
        assertEquals("table.field AS alias BETWEEN table.first AND table.last", node.toString());
    }

    /**
     * Test for {@link BetweenNode#toString()} method.
     */
    @Test
    void testToString() {
        final FieldNode field = new FieldNode("table", "field", null);
        final FieldNode first = new FieldNode("table", "first", null);
        final FieldNode last = new FieldNode("table", "last", null);
        final BetweenNode node = new BetweenNode(field, first, last, null);
        assertEquals("table.field BETWEEN table.first AND table.last", node.toString());
    }

    /**
     * Test for between condition.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testInsensitiveLike() throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select \"date\" from fields.date7 where \"date\" between '2018-01-02' and '2018-01-02'")) {

            assertTrue(rs.next());
            assertEquals("2018-01-02", rs.getString("date"));
            assertFalse(rs.next());
        }
    }
}
