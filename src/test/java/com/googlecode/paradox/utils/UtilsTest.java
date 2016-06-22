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

    private Connection conn;

    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName(Driver.class.getName());
    }

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

    @Test
    public void testIsNotWrapFor() throws Exception {
        Assert.assertFalse(Utils.isWrapperFor(conn, Connection.class));
    }

    @Test
    public void testIsWrapFor() throws Exception {
        Utils.unwrap(conn, ParadoxConnection.class);
    }

    @Test
    public void testUnwrap() throws Exception {
        Assert.assertTrue(Utils.isWrapperFor(conn, ParadoxConnection.class));
    }

    @Test(expected=SQLException.class)
    public void testUnwrapImpossive() throws Exception {
        Utils.unwrap(conn, Integer.class);
    }
}
