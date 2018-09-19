package com.googlecode.paradox.integration;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.planner.Planner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Integration test for SQL planning.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
@Category(IntegrationTest.class)
public class PlannerTest {

    /**
     * The database test connection.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Register the driver.
     *
     * @throws ClassNotFoundException in case of connection errors.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Used to close the test connection.
     *
     * @throws Exception in case closing of errors.
     */
    @After
    public void closeConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws Exception in case of connection errors.
     */
    @Before
    public void connect() throws Exception {
        this.conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for an invalid table.
     *
     * @throws Exception in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testInvalidTable() throws Exception {
        final SQLParser parser = new SQLParser("select * from invalid");
        final Planner planner = new Planner(this.conn);
        planner.create(parser.parse().get(0), this.conn.getCurrentSchema());
    }

}
