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
package com.googlecode.paradox.parser.nodes;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link AsteriskNode}.
 *
 * @version 1.1
 * @since 1.5.0
 */
public class AsteriskNodeTest {
    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    @SuppressWarnings("java:S2115")
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for instance.
     */
    @Test
    public void testInstance() {
        final AsteriskNode node = new AsteriskNode(conn, null);
        Assert.assertEquals("Test for node name", "ASTERISK", node.getName());
    }

    /**
     * Test toString method.
     */
    @Test
    public void testToString() {
        AsteriskNode node = new AsteriskNode(conn, null);
        Assert.assertEquals("Invalid value", "*", node.toString());

        node = new AsteriskNode(conn, "t");
        Assert.assertEquals("Invalid value", "t.*", node.toString());
    }
}
