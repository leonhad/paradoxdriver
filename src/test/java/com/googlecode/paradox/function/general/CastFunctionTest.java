package com.googlecode.paradox.function.general;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.function.string.RightFunction;
import org.junit.*;

import java.sql.*;

/**
 * Unit test for {@link RightFunction}.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class CastFunctionTest {

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
    public CastFunctionTest() {
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
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for cast function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCast() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select CAST('1234' as INTEGER) ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid column type", Types.INTEGER, rs.getMetaData().getColumnType(1));
            Assert.assertEquals("Invalid column type", "INTEGER", rs.getMetaData().getColumnTypeName(1));
            Assert.assertEquals("Invalid value", 1234, rs.getInt(1));
            Assert.assertTrue("Invalid value", rs.getObject(1) instanceof Integer);
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for Blob.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testBlob() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select CAST('1234' as BLOB) ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());

            Assert.assertEquals("Invalid column type", Types.BLOB, rs.getMetaData().getColumnType(1));
            Assert.assertEquals("Invalid column type", "BLOB", rs.getMetaData().getColumnTypeName(1));
            Assert.assertTrue("Invalid value", rs.getObject(1) instanceof byte[]);

            Blob blob = rs.getBlob(1);
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}