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
import com.googlecode.paradox.procedures.math.Min;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link ProcedureAS}.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ProcedureASTest {
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
     * Test for get all procedures available.
     */
    @Test
    public void testAllProcedures() {
        Assert.assertEquals(5, new ProcedureAS(conn).list().size());
    }

    /**
     * Test instance.
     */
    @Test
    public void testInstance() {
        Assert.assertNotNull(new ProcedureAS(conn));
    }

    /**
     * Test for an invalid procedure.
     */
    @Test
    public void testInvalidProcedure() {
        Assert.assertNull(new ProcedureAS(conn).get("INVALID"));
    }

    /**
     * Test the get procedure by its name.
     */
    @Test
    public void testProcedureByName() {
        final Min min = new Min(conn);
        final AbstractCallableProcedure minByName = new ProcedureAS(conn).get("min");
        Assert.assertNotNull("Procedure not registered.", minByName);
        Assert.assertSame("Procedure is not the same.", min.getName(), minByName.getName());
    }
}
