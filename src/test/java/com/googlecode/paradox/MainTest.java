package com.googlecode.paradox;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * Register the database driver.
     *
     * @throws ClassNotFoundException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Test the data and time.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDateTime() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "date");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT \"DATE\", \"TIME\" FROM DATE7")) {

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertEquals("Invalid time.", "10:00:00", rs.getTime("TIME").toString());
            Assert.assertEquals("Invalid date.", "2018-01-01", rs.getDate("DATE").toString());

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertEquals("Invalid time.", "10:30:00", rs.getTime("TIME").toString());
            Assert.assertEquals("Invalid date.", "2018-02-01", rs.getDate("DATE").toString());

            Assert.assertTrue("Invalid row state.", rs.next());
            Assert.assertEquals("Invalid time.", "09:25:25", rs.getTime("TIME").toString());
            Assert.assertEquals("Invalid date.", "2018-01-02", rs.getDate("DATE").toString());

            Assert.assertFalse("Invalid row state.", rs.next());
        }
    }
}
