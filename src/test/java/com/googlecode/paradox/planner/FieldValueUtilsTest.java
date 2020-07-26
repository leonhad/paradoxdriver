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

package com.googlecode.paradox.planner;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.utils.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link FieldValueUtils} class.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class FieldValueUtilsTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The connection.
     */
    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "fields");
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for sanity.
     */
    @Test
    public void testSanity() {
        Assert.assertTrue("Utility class in wrong format.", TestUtil.assertSanity(FieldValueUtils.class));
    }

    /**
     * Test charset conversion.
     *
     * @throws ParadoxDataException in case of conversion failures.
     */
    @Test
    public void testConversion() throws ParadoxDataException {
        final String original = "String to convert.";
        final byte[] values = original.getBytes(StandardCharsets.UTF_8);

        Assert.assertEquals("Invalid conversion", original, FieldValueUtils.convert(values, StandardCharsets.UTF_8));
    }
}