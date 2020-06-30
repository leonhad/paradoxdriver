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
package com.googlecode.paradox.data.field;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.metadata.ParadoxTable;
import com.googlecode.paradox.results.ParadoxFieldType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link VarcharField} class.
 *
 * @author Leonardo Costa
 * @version 1.1
 * @since 1.3
 */
public class VarcharFieldTest {
    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "date");
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for invalid match.
     */
    @Test
    public void testInvalidMatch() {
        final VarcharField field = new VarcharField();
        Assert.assertFalse("Invalid field value.", field.match(0));
    }

    /**
     * Test for parse method.
     */
    @Test
    public void testParse() {
        final ParadoxTable table = new ParadoxTable(null, null, null);
        table.setCharset(StandardCharsets.ISO_8859_1);
        final ParadoxField paradoxField = new ParadoxField(conn, ParadoxFieldType.VARCHAR.getType());
        paradoxField.setSize("test".length());
        final VarcharField field = new VarcharField();
        final ByteBuffer buffer = ByteBuffer.wrap("test".getBytes(table.getCharset()));
        final FieldValue value = field.parse(table, buffer, paradoxField);
        Assert.assertEquals("Value not equals.", "test", value.getValue());
    }

    /**
     * Test for valid match.
     */
    @Test
    public void testValidMatch() {
        final VarcharField field = new VarcharField();
        Assert.assertTrue("Field doesn't match.", field.match(1));
    }
}
