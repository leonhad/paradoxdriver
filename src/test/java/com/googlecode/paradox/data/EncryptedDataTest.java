package com.googlecode.paradox.data;

import com.googlecode.paradox.Driver;
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
    public void testAcq() throws SQLException {
        try (final Connection conn = DriverManager.getConnection(CONNECTION_STRING + "db");
             final Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM encrypted")) {
            while (rs.next()) {
                //System.out.println(rs.getString(1));
            }
        }
    }

}