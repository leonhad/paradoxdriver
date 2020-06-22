/*
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.planner;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.integration.MainTest;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.parser.nodes.IdentifierNode;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.planner.plan.SelectPlan;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * Unit test for {@link Planner}.
 *
 * @author Leonardo Alves da Costa
 * @version 1.1
 * @since 1.1
 */
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
     * Test for a asterisk node plan.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testAsterisk() throws Exception {
        final SQLParser parser = new SQLParser(conn, "select * from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        Assert.assertNotNull("No columns.", plan.getColumns());
        Assert.assertEquals("Number of columns in table.", 3, plan.getColumns().size());
        Assert.assertEquals("First column not 'AC'.", "AC", plan.getColumns().get(0).getName());
        Assert.assertEquals("Second column not 'State'.", "State", plan.getColumns().get(1).getName());
        Assert.assertEquals("Third column not 'Cities'.", "Cities", plan.getColumns().get(2).getName());
    }

    /**
     * Test for a asterisk node plan.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testAsteriskWithTables() throws Exception {
        final SQLParser parser = new SQLParser(conn, "select a.* from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        Assert.assertNotNull("No columns.", plan.getColumns());
        Assert.assertEquals("Number of columns in table.", 3, plan.getColumns().size());
        Assert.assertEquals("First column not 'AC'.", "AC", plan.getColumns().get(0).getName());
        Assert.assertEquals("Second column not 'State'.", "State", plan.getColumns().get(1).getName());
        Assert.assertEquals("Third column not 'Cities'.", "Cities", plan.getColumns().get(2).getName());
    }

    /**
     * Test for valid column name.
     *
     * @throws SQLException if there is no errors.
     */
    @Test
    public void testColumnName() throws SQLException {
        final SQLParser parser = new SQLParser(conn, "select ac from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        Assert.assertEquals("Test the column size.", 1, plan.getColumns().size());
        Assert.assertEquals("Test the column name.", "AC", plan.getColumns().get(0).getName());
    }

    /**
     * Test for empty column name.
     *
     * @throws SQLException if there is no errors.
     */
    @Test(expected = SQLException.class)
    public void testEmptyColumnName() throws SQLException {
        final SelectNode selectNode = new SelectNode(conn);
        selectNode.addField(new IdentifierNode(conn, ""));
        Planner.create(conn, selectNode);
    }

    /**
     * Test for a invalid node.
     *
     * @throws SQLException if there is no errors.
     */
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testInvalid() throws SQLException {
        final StatementNode node = new StatementNode(conn, "node");
        Planner.create(conn, node);
    }

    /**
     * Test for an invalid table.
     *
     * @throws Exception in case of failures.
     */
    @Test(expected = SQLException.class)
    public void testInvalidTable() throws Exception {
        final SQLParser parser = new SQLParser(conn, "select * from invalid");
        Planner.create(conn, parser.parse().get(0));
    }

    /**
     * Test for null column name.
     *
     * @throws SQLException if there is no errors.
     */
    @Test(expected = SQLException.class)
    public void testNullColumnName() throws SQLException {
        final SelectNode selectNode = new SelectNode(conn);
        selectNode.addField(new IdentifierNode(conn, null));
        Planner.create(conn, selectNode);
    }

    /**
     * Test for SELECT plan without columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testSelectWithoutColumns() throws SQLException {
        final SelectNode node = new SelectNode(conn);
        Planner.create(conn, node);
    }

    /**
     * Test for SELECT plan with where equals clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereEquals() throws SQLException {
        final SQLParser parser = new SQLParser(conn,
                "select ac from areacodes where state = ny and ac = 212 or ac=315 or ac=917");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        plan.execute();
        Assert.assertEquals("Test the result size.", 3, plan.getValues().size());
        Assert.assertEquals("Test the result value.", "212", plan.getValues().get(0)[0].getValue());
        Assert.assertEquals("Test the result value.", "315", plan.getValues().get(1)[0].getValue());
        Assert.assertEquals("Test the result value.", "917", plan.getValues().get(2)[0].getValue());
    }

    /**
     * Test for SELECT plan with where notEquals clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereNotEquals() throws SQLException {
        final SQLParser parser = new SQLParser(conn,
                "select ac from areacodes where state <> ny and ac = 212 or ac=315 or ac=917");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        plan.execute();
        Assert.assertEquals("Test the result size.", 0, plan.getValues().size());
    }

    /**
     * Test for SELECT plan with where GreaterThan clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereGreaterThan() throws SQLException {
        final SQLParser parser = new SQLParser(conn, "select ac from areacodes where state = ny and ac > 845");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        plan.execute();
        Assert.assertEquals("Test the result size.", 2, plan.getValues().size());
    }

    /**
     * Test for SELECT plan with where LessThan clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereLessThan() throws SQLException {
        final SQLParser parser = new SQLParser(conn, "select ac from areacodes where state = ny and ac < 320");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        plan.execute();
        Assert.assertEquals("Test the result size.", 2, plan.getValues().size());
    }

    /**
     * Test for SELECT plan with where clause and multiples columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereMultipleColumns() throws SQLException {
        final SQLParser parser = new SQLParser(conn, "select * from areacodes where state = ny and ac < 320");
        final SelectPlan plan = (SelectPlan) Planner.create(conn, parser.parse().get(0));
        plan.execute();
        Assert.assertEquals("Test the result size.", 2, plan.getValues().size());
        Assert.assertEquals("Field expected", "AC", plan.getValues().get(0)[0].getField().getName());
        Assert.assertEquals("Field expected", "State", plan.getValues().get(0)[1].getField().getName());
        Assert.assertEquals("Field expected", "Cities", plan.getValues().get(0)[2].getField().getName());
    }

}
