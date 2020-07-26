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
import com.googlecode.paradox.data.IndexData;
import com.googlecode.paradox.data.PrimaryKeyData;
import com.googlecode.paradox.data.TableData;
import com.googlecode.paradox.data.ViewData;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link Utils}.
 *
 * @version 1.3
 * @since 1.2
 */
public class UtilsTest {

    /**
     * Connection string used in tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * Database connection.
     */
    private Connection conn;

    /**
     * Register the driver.
     *
     * @throws Exception in case of failures.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    /**
     * Close the test connection.
     *
     * @throws Exception in case of failures.
     */
    @After
    public void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws Exception in case of failures.
     */
    @Before
    public void connect() throws Exception {
        this.conn = DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test if the constructor is private.
     */
    @Test
    public void testConstructorIsPrivate() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(Utils.class));
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)} method
     * with invalid value.
     */
    @Test
    public void testIsNotWrapFor() {
        Assert.assertFalse("Invalid wrapper value.", Utils.isWrapperFor(this.conn, Connection.class));
    }

    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)}.
     */
    @Test
    public void testIsWrapFor() {
        Assert.assertTrue("Invalid wrapper value.", Utils.isWrapperFor(this.conn, ParadoxConnection.class));
    }

    /**
     * Test for unwrap.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testUnwrap() throws Exception {
        Assert.assertNotNull("Invalid class instance.", Utils.unwrap(this.conn, ParadoxConnection.class));
    }

    /**
     * Test for a unwrap with wrong class.
     *
     * @throws Exception in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testUnwrapImpossible() throws Exception {
        Utils.unwrap(this.conn, Integer.class);
    }

    /**
     * Test for a valid connection.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testValidConnection() throws SQLException {
        Assert.assertTrue("Wrapper invalid.", this.conn.isWrapperFor(ParadoxConnection.class));
        Assert.assertNotNull("Can't unwrap.", this.conn.unwrap(ParadoxConnection.class));
    }

    /**
     * Test for remove suffix extension.
     */
    @Test
    public void testRemoveSuffix() {
        Assert.assertEquals("Invalid file name.", "FILE", Utils.removeSuffix("FILE.DB", "DB"));
        Assert.assertEquals("Invalid file name.", "FILE", Utils.removeSuffix("FILE", "DB"));
        Assert.assertEquals("Invalid file name.", "FILE.TXT", Utils.removeSuffix("FILE.TXT", "DB"));
        Assert.assertNull("Invalid file name.", Utils.removeSuffix(null, null));
    }

    /**
     * Test for utilities classes.
     */
    @Test
    public void testClassesIntegrity() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(Utils.class));
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(DateUtils.class));
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(TableData.class));
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(ViewData.class));
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(Expressions.class));
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(IndexData.class));
        Assert.assertTrue("Utility class in wrong format.",
                TestUtil.assertSanity(PrimaryKeyData.class));
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(Constants.class));
    }
}
