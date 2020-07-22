package com.googlecode.paradox.function.date;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Unit test for {@link TimeFunction}.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class TimeFunctionTest {

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
    public TimeFunctionTest() {
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
     * Test for time function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testTime() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select TIME('01:02:03') ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNotNull("Invalid time", rs.getTime(1));
            Assert.assertEquals("Invalid time", "01:02:03", rs.getTime(1).toString());
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for time with date.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDate() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select TIME('2020-01-01') ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNotNull("Invalid time", rs.getTime(1));
            Assert.assertEquals("Invalid time", "00:00:00", rs.getTime(1).toString());
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for time with timestamp.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testTimestamp() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select TIME('2020-01-01 01:02:03') ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNotNull("Invalid time", rs.getTime(1));
            Assert.assertEquals("Invalid time", "01:02:03", rs.getTime(1).toString());
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for invalid time.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInvalid() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select TIME('a') ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNull("Invalid time", rs.getTime(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for null value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select TIME(null) ");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNull("Invalid time", rs.getTime(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}