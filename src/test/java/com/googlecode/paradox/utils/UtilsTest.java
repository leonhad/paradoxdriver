package com.googlecode.paradox.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.integration.MainTest;

public class UtilsTest {
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

    private Connection conn;

    @After
    public void closeConnection() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    @Before
    public void connect() throws Exception {
        conn = DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    @Test(expected=SQLException.class)
    public void testIsNotWrapFor() throws Exception {
        Utils.unwrap(conn, Integer.class);
    }

    @Test
    public void testIsWrapFor() throws Exception {
        Utils.unwrap(conn, ParadoxConnection.class);
    }

    @Test
    public void testUnwrap() throws Exception {
        Assert.assertTrue(Utils.isWrapperFor(conn, ParadoxConnection.class));
    }

    @Test
    public void testUnwrapImpossive() throws Exception {
        Assert.assertFalse(Utils.isWrapperFor(conn, Connection.class));
    }
}
