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
package com.googlecode.paradox.utils;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link Expressions}.
 *
 * @version 1.1
 * @since 1.1
 */
public class ExpressionsTest {

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
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for equals.
     */
    @Test
    public void testEquals() {
        Assert.assertTrue("Invalid value.", Expressions.accept(conn, "TABLE", "table", false));
    }

    /**
     * Test for an extra value.
     */
    @Test
    public void testExtra() {
        Assert.assertFalse("Invalid value.", Expressions.accept(conn, "TEST.QBE~", "%.QBE", false));
    }

    /**
     * Test for like.
     */
    @Test
    public void testLikes() {
        Assert.assertTrue("Invalid value.", Expressions.accept(conn, "TABLE", "TA%", false));
    }

    /**
     * Test for mixed values.
     */
    @Test
    public void testMix() {
        Assert.assertTrue("Invalid value.", Expressions.accept(conn, "TEST.X02", "%.X__", false));
    }

    /**
     * Test for not equals.
     */
    @Test
    public void testNotEquals() {
        Assert.assertFalse("Invalid value.", Expressions.accept(conn, "TABLE", "table", true));
    }

    /**
     * Test for sanity.
     */
    @Test
    public void testSanity() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertUtilityClassWellDefined(Expressions.class));
    }

    /**
     * Test for unique keyword.
     */
    @Test
    public void testUnique() {
        Assert.assertTrue("Invalid value.", Expressions.accept(conn, "TABLE", "TAB_E", false));
    }
}
