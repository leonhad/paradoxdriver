package com.googlecode.paradox.function.string;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Unit test for {@link VarcharFunction}.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class VarcharFunctionTest {

    /**
     * The connection string used in this tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Creates a new instance.
     */
    public VarcharFunctionTest() {
        super();
    }

    /**
     * Register the database driver.
     */
    @BeforeClass
    public static void initClass() {
        new Driver();
    }

    /**
     * Close the test connection.
     *
     * @throws SQLException in case of failures.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws SQLException in case of failures.
     */
    @Before
    @SuppressWarnings("java:S2115")
    public void connect() throws SQLException {
        Properties properties = new Properties();
        properties.put("locale", "pt-BR");
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db", properties);
    }

    /**
     * Test for numeric values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNumber() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select varchar(1.2) ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid value", "1.2", rs.getString(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}