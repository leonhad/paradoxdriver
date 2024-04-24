package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.metadata.paradox.ParadoxTable;
import com.googlecode.paradox.metadata.paradox.ParadoxValidation;
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
     * Test for invalid table
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInvalidTable() throws SQLException {
        List<Table> validations = this.conn.getConnectionInfo().getCurrentSchema().list(this.conn.getConnectionInfo(), "AREACODE");
        Assert.assertFalse(validations.isEmpty());
        Assert.assertTrue(validations.get(0) instanceof ParadoxTable);
        ParadoxTable table = (ParadoxTable) validations.get(0);
        Assert.assertNotNull(table.getValidation());

        ParadoxValidation validation = table.getValidation();
        Assert.assertEquals("###", validation.getFields()[0].getMask());
        Assert.assertEquals("Area Code", validation.getFields()[0].getName());
        Assert.assertEquals("&&", validation.getFields()[1].getMask());
        Assert.assertEquals("State", validation.getFields()[1].getName());
    }
}
