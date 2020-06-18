package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

public class EncryptedDataTest {

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

    @Test
    public void testExcrypted() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "encrypt");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM encrypted")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 1, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Value 1", rs.getString("Text"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 2, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Value 2", rs.getString("Text"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 3, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Value 3", rs.getString("Text"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 4, rs.getInt("Id"));
            Assert.assertEquals("Invalid id value", "Last one", rs.getString("Text"));

            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }

    @Test
    public void testExcrypted35() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "encrypt");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM encrypted35")) {

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 1.0D, rs.getDouble("A"), 0.0001D);
            Assert.assertEquals("Invalid id value", "Test 1", rs.getString("B"));

            Assert.assertTrue("Invalid ResultSet state.", rs.next());
            Assert.assertEquals("Invalid id value", 2.0D, rs.getInt("A"), 0.0001D);
            Assert.assertEquals("Invalid id value", "Test2", rs.getString("B"));

            Assert.assertFalse("Invalid ResultSet state.", rs.next());
        }
    }
}