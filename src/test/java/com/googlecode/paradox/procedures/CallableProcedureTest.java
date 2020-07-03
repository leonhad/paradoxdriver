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
package com.googlecode.paradox.procedures;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.procedures.math.Average;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Unit test for {@link AbstractCallableProcedure}.
 *
 * @version 1.0
 * @since 1.3
 */
public class CallableProcedureTest {

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
     * The object to test.
     */
    private final AbstractCallableProcedure call = new Average(conn);

    /**
     * Test for default field.
     */
    @Test
    public void testDefaultField() {
        final List<ParadoxField> fields = this.call.getCols();
        Assert.assertEquals("Invalid field size.", 1, fields.size());
        Assert.assertEquals("Invalid field name.", "field", fields.get(0).getName());
        Assert.assertEquals("Invalid field type.", 0xC, fields.get(0).getType());
    }

    /**
     * Test for return type procedure.
     */
    @Test
    public void testReturnType() {
        Assert.assertEquals("Invalid return type.", DatabaseMetaData.procedureReturnsResult, this.call.getReturnType());
    }
}
