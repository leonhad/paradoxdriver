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

package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxReferentialIntegrity;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.metadata.paradox.ParadoxValidation;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link ValidationData}.
 *
 * @since 1.6.1
 */
public class ValidationDataTest {

    /**
     * Connection string used in tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    public ValidationDataTest() {
        super();
    }

    /**
     * Register the driver.
     */
    @BeforeAll
    static void initClass() {
        new Driver();
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @AfterEach
    void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @BeforeEach
    void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for picture validation
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testPictureValidationAreaCode() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getCurrentSchema().list(this.conn.getConnectionInfo(), "AREACODE");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        ParadoxTable table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        assertEquals(4, validation.getFields().length);
        assertEquals("###", validation.getFields()[0].getPicture());
        assertEquals("Area Code", validation.getFields()[0].getName());
        assertNull(validation.getFields()[1].getPicture());
        assertEquals("Country", validation.getFields()[1].getName());
        assertNull(validation.getFields()[2].getPicture());
        assertEquals("Full State", validation.getFields()[2].getName());
        assertEquals("&&", validation.getFields()[3].getPicture());
        assertEquals("State", validation.getFields()[3].getName());

        validations = this.conn.getConnectionInfo().getSchema(null, "areas").list(this.conn.getConnectionInfo(), "ZIPCODES");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        validation = table.getValidation();
        assertEquals("#####", validation.getFields()[0].getPicture());
        assertEquals("Zip", validation.getFields()[0].getName());
        assertEquals("&&", validation.getFields()[1].getPicture());
        assertEquals("State", validation.getFields()[1].getName());
    }

    /**
     * Test for picture validation
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testPictureValidationZipCode() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "areas").list(this.conn.getConnectionInfo(), "ZIPCODES");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        ParadoxTable table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        assertEquals("#####", validation.getFields()[0].getPicture());
        assertEquals("Zip", validation.getFields()[0].getName());
        assertEquals("&&", validation.getFields()[1].getPicture());
        assertEquals("State", validation.getFields()[1].getName());
    }

    /**
     * Test for default validation.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDefaultValue() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "fields").list(this.conn.getConnectionInfo(), "logical");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        ParadoxTable table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        assertEquals("BOOL", validation.getFields()[0].getName());
        assertEquals(ParadoxType.BOOLEAN, validation.getFields()[0].getType());
        assertNotNull(validation.getFields()[0].getDefaultValue());
        assertTrue((Boolean) validation.getFields()[0].getDefaultValue());
    }

    /**
     * Test for table lookup.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTableLookup() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "joins").list(this.conn.getConnectionInfo(), "origin");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        ParadoxTable table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        assertEquals("FK", validation.getFields()[1].getName());
        assertEquals(ParadoxType.LONG, validation.getFields()[1].getType());
        assertEquals("destination.db", validation.getFields()[1].getReferencedTableName());
        assertFalse(validation.getFields()[1].isLookupAllFields());
        assertTrue(validation.getFields()[1].isLookupHelp());
    }

    /**
     * Test for referential integrity.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFk1() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "joins").list(this.conn.getConnectionInfo(), "fk1");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        ParadoxTable table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        ParadoxReferentialIntegrity[] references = validation.getReferentialIntegrity();
        assertEquals(1, references.length);
        assertEquals("reference", references[0].getName());
        assertEquals("primary.db", references[0].getDestinationTableName());
        assertTrue(references[0].isCascade());
    }

    /**
     * Test for required fields.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testRequired() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "joins").list(this.conn.getConnectionInfo(), "paradox-ascii");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        ParadoxTable table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        assertEquals("A", validation.getFields()[0].getName());
        assertTrue(validation.getFields()[0].isRequired());
        assertEquals(ParadoxType.VARCHAR, validation.getFields()[0].getType());
    }

    /**
     * Test for table with multiple options.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testMultiple() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "joins").list(this.conn.getConnectionInfo(), "multiple");
        assertFalse(validations.isEmpty());
        assertInstanceOf(ParadoxTable.class, validations.get(0));
        ParadoxTable table = (ParadoxTable) validations.get(0);
        assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        assertEquals(2, validation.getCount());

        assertEquals(4, validation.getFieldCount());
        assertEquals("ID", validation.getFields()[0].getName());
        assertEquals("FK", validation.getFields()[1].getName());
        assertEquals("A", validation.getFields()[2].getName());
        assertEquals("FK2", validation.getFields()[3].getName());

        assertNull(validation.getFields()[0].getPicture());
        assertNull(validation.getFields()[1].getPicture());
        assertEquals("[(*3{#}) ]*3{#}-*4{#}", validation.getFields()[2].getPicture());
        assertNull(validation.getFields()[3].getPicture());

        ParadoxReferentialIntegrity[] references = validation.getReferentialIntegrity();
        assertEquals("fk2", references[0].getName());
        assertEquals("primary.db", references[0].getDestinationTableName());
        assertEquals(4, references[0].getFields()[0]);
        assertTrue(references[0].isCascade());

        assertEquals("fk_multiple_primary", references[1].getName());
        assertEquals("primary.db", references[1].getDestinationTableName());
        assertEquals(2, references[1].getFields()[0]);
        assertTrue(references[1].isCascade());
    }
}
