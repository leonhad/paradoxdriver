package com.googlecode.paradox.integration;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.planner.Planner;
import com.googlecode.paradox.planner.plan.SelectPlan;

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
     * @throws ClassNotFoundException
     *             in case of connection errors.
     */
    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    /**
     * Used to close the test connection.
     * 
     * @throws Exception
     *             in case closing of errors.
     */
    @After
    public void closeConnection() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Connect to test database.
     * 
     * @throws Exception
     *             in case of connection errors.
     */
    @Before
    public void connect() throws Exception {
        conn = (ParadoxConnection) DriverManager.getConnection(MainTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for an invalid table.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testInvalidTable() throws Exception {
        final SQLParser parser = new SQLParser("select * from invalid");
        final Planner planner = new Planner(conn);
        planner.create(parser.parse().get(0));
    }

    /**
     * Test for a SELECT plan.
     * 
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testSelect() throws Exception {
        final SQLParser parser = new SQLParser("select * from areacodes a");
        final Planner planner = new Planner(conn);
        final SelectPlan plan = (SelectPlan) planner.create(parser.parse().get(0));
        Assert.assertNotNull("No columns.", plan.getColumns());
        Assert.assertEquals("Number of columns in table.", 3, plan.getColumns().size());
        Assert.assertEquals("First column not 'AC'.", "AC", plan.getColumns().get(0).getName());
        Assert.assertEquals("Second column not 'State'.", "STATE", plan.getColumns().get(1).getName());
        Assert.assertEquals("Third column not 'Cities'.", "CITIES", plan.getColumns().get(2).getName());
    }
}
