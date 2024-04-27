package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.metadata.paradox.ParadoxValidation;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

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
    @BeforeClass
    public static void initClass() {
        new Driver();
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @Before
    public void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for picture validation
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testPictureValidationAreaCode() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getCurrentSchema().list(this.conn.getConnectionInfo(), "AREACODE");
        Assert.assertFalse(validations.isEmpty());
        Assert.assertTrue(validations.get(0) instanceof ParadoxTable);
        ParadoxTable table = (ParadoxTable) validations.get(0);
        Assert.assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        Assert.assertEquals(4, validation.getFields().length);
        Assert.assertEquals("###", validation.getFields()[0].getPicture());
        Assert.assertEquals("Area Code", validation.getFields()[0].getName());
        Assert.assertNull(validation.getFields()[1].getPicture());
        Assert.assertEquals("Country", validation.getFields()[1].getName());
        Assert.assertNull(validation.getFields()[2].getPicture());
        Assert.assertEquals("Full State", validation.getFields()[2].getName());
        Assert.assertEquals("&&", validation.getFields()[3].getPicture());
        Assert.assertEquals("State", validation.getFields()[3].getName());

        validations = this.conn.getConnectionInfo().getSchema(null, "areas").list(this.conn.getConnectionInfo(), "ZIPCODES");
        Assert.assertFalse(validations.isEmpty());
        Assert.assertTrue(validations.get(0) instanceof ParadoxTable);
        table = (ParadoxTable) validations.get(0);
        Assert.assertNotNull(table.getValidation());

        validation = table.getValidation();
        Assert.assertEquals("#####", validation.getFields()[0].getPicture());
        Assert.assertEquals("Zip", validation.getFields()[0].getName());
        Assert.assertEquals("&&", validation.getFields()[1].getPicture());
        Assert.assertEquals("State", validation.getFields()[1].getName());
    }

    /**
     * Test for picture validation
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testPictureValidationZipCode() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "areas").list(this.conn.getConnectionInfo(), "ZIPCODES");
        Assert.assertFalse(validations.isEmpty());
        Assert.assertTrue(validations.get(0) instanceof ParadoxTable);
        ParadoxTable table = (ParadoxTable) validations.get(0);
        Assert.assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        Assert.assertEquals("#####", validation.getFields()[0].getPicture());
        Assert.assertEquals("Zip", validation.getFields()[0].getName());
        Assert.assertEquals("&&", validation.getFields()[1].getPicture());
        Assert.assertEquals("State", validation.getFields()[1].getName());
    }

    /**
     * Test for default validation.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDefaultValue() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "fields").list(this.conn.getConnectionInfo(), "logical");
        Assert.assertFalse(validations.isEmpty());
        Assert.assertTrue(validations.get(0) instanceof ParadoxTable);
        ParadoxTable table = (ParadoxTable) validations.get(0);
        Assert.assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        Assert.assertEquals("BOOL", validation.getFields()[0].getName());
        Assert.assertEquals(ParadoxType.BOOLEAN, validation.getFields()[0].getType());
        Assert.assertNotNull(validation.getFields()[0].getDefaultValue());
        Assert.assertTrue((Boolean) validation.getFields()[0].getDefaultValue());
    }

    /**
     * Test for table lookup.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testTableLookup() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getSchema(null, "joins").list(this.conn.getConnectionInfo(), "origin");
        Assert.assertFalse(validations.isEmpty());
        Assert.assertTrue(validations.get(0) instanceof ParadoxTable);
        ParadoxTable table = (ParadoxTable) validations.get(0);
        Assert.assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        Assert.assertEquals("ID", validation.getFields()[0].getName());
        Assert.assertEquals(ParadoxType.AUTO_INCREMENT, validation.getFields()[0].getType());
        Assert.assertNotNull(validation.getFields()[0].getDefaultValue());
        Assert.assertTrue((Boolean) validation.getFields()[0].getDefaultValue());
    }
}
