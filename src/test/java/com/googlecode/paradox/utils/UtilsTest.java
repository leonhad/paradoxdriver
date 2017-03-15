/*
 * UtilsTest.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.integration.MainTest;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for {@link Utils}.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
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
     * @throws Exception
     *             in case of failures.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }
    
    /**
     * Close the test connection.
     *
     * @throws Exception
     *             in case of failures.
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
     * @throws Exception
     *             in case of failures.
     */
    @Before
    public void connect() throws Exception {
        this.conn = DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }
    
    /**
     * Test if the constructor is private.
     *
     * @throws NoSuchMethodException
     *             in case of failures.
     * @throws IllegalAccessException
     *             in case of failures.
     * @throws InvocationTargetException
     *             in case of failures.
     * @throws InstantiationException
     *             in case of failures.
     */
    @Test
    public void testConstructorIsPrivate()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertUtilityClassWellDefined(Utils.class));
    }
    
    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)} method
     * with invalid value.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testIsNotWrapFor() throws Exception {
        Assert.assertFalse(Utils.isWrapperFor(this.conn, Connection.class));
    }
    
    /**
     * Test for the {@link Utils#isWrapperFor(java.sql.Wrapper, Class)}.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testIsWrapFor() throws Exception {
        Assert.assertTrue(Utils.isWrapperFor(this.conn, ParadoxConnection.class));
    }
    
    /**
     * Test for unwrap.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testUnwrap() throws Exception {
        Assert.assertNotNull(Utils.unwrap(this.conn, ParadoxConnection.class));
    }
    
    /**
     * Test for a unwrap with wrong class.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testUnwrapImpossible() throws Exception {
        Utils.unwrap(this.conn, Integer.class);
    }
}
