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

package com.googlecode.paradox.planner.plan;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.parser.nodes.TableNode;
import com.googlecode.paradox.planner.Planner;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.PlanTableNode;
import com.googlecode.paradox.planner.nodes.comparable.EqualsNode;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.results.Column;
import org.junit.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Unit test for {@link SelectPlan} class.
 *
 * @version 1.5
 * @since 1.3
 */
@SuppressWarnings({"java:S109", "java:S1192"})
public class SelectPlanTest {

    /**
     * The connection string used in this tests.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";
    private static final String AREA_CODES = "areacodes";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Creates a new instance.
     */
    public SelectPlanTest() {
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
        this.conn = (ParadoxConnection) DriverManager.getConnection(SelectPlanTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for column value with table alias.
     *
     * @throws SQLException if has errors.
     */
    @Test
    public void testColumnWithTableAlias() throws SQLException {
        final SelectPlan plan = new SelectPlan(null, false);

        TableNode table = new TableNode(null, AREA_CODES, "test", null);

        PlanTableNode tableNode = new PlanTableNode();
        tableNode.setTable(conn, table);
        plan.addTable(tableNode);

        plan.addColumn(new FieldNode("test", "ac", null, null));
        Assert.assertEquals("Invalid column size.", 1, plan.getColumns().size());
    }

    /**
     * Test for invalid column value.
     *
     * @throws SQLException if there are no errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidColumn() throws SQLException {
        final SelectPlan plan = new SelectPlan(null, false);
        plan.addColumn(new FieldNode(null, "invalid", null, null));
    }

    /**
     * Test for invalid table alias.
     *
     * @throws SQLException if has errors.
     */
    @Test(expected = SQLException.class)
    public void testInvalidTableAlias() throws SQLException {
        final SelectPlan plan = new SelectPlan(null, false);

        TableNode table = new TableNode(null, AREA_CODES, "test", null);

        PlanTableNode tableNode = new PlanTableNode();
        tableNode.setTable(conn, table);
        plan.addTable(tableNode);

        plan.addColumn(new FieldNode("test2", "ac", null, null));
    }

    /**
     * Test DISTINCT.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDistinct() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select distinct REQTYPE from server");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "G", rs.getString("REQTYPE"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "P", rs.getString("REQTYPE"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for SELECT plan performance optimizations.
     *
     * @throws SQLException if has errors.
     */
    @Test
    public void testSelectJoinOptimization() throws SQLException {
        final SQLParser parser = new SQLParser("select distinct 1 from geog.tblAC ac, geog.tblsttes st, geog.County c" +
                " where c.StateID = st.State and st.State = ac.State and c.CountyID = 201");
        final List<StatementNode> list = parser.parse();
        Assert.assertEquals("Invalid list size", 1, list.size());

        final Plan plan = Planner.create(conn, list.get(0));
        Assert.assertTrue("Invalid select plan instance", plan instanceof SelectPlan);

        final SelectPlan selectPlan = (SelectPlan) plan;

        // Remove the conditionals.
        Assert.assertNull("Invalid join clause", selectPlan.getCondition());

        Assert.assertEquals("Invalid table count", 3, selectPlan.getTables().size());
        Assert.assertTrue("Invalid table condition",
                selectPlan.getTables().get(1).getConditionalJoin() instanceof EqualsNode);
        Assert.assertTrue("Invalid table condition",
                selectPlan.getTables().get(2).getConditionalJoin() instanceof ANDNode);
    }

    /**
     * Test for SELECT plan performance optimizations with OR.
     *
     * @throws SQLException if has errors.
     */
    @Test
    public void testSelectJoinOptimizationOr() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select distinct 1 from geog.tblAC ac, geog.tblsttes st, geog.County c " +
                        "where c.StateID = st.State and st.State = ac.State or c.CountyID = 201");
        final List<StatementNode> list = parser.parse();
        Assert.assertEquals("Invalid list size", 1, list.size());

        final Plan plan = Planner.create(conn, list.get(0));
        Assert.assertTrue("Invalid select plan instance", plan instanceof SelectPlan);

        final SelectPlan selectPlan = (SelectPlan) plan;

        // Remove the conditionals.
        Assert.assertNotNull("Invalid join clause", selectPlan.getCondition());

        Assert.assertEquals("Invalid table count", 3, selectPlan.getTables().size());
        Assert.assertNull("Invalid table condition", selectPlan.getTables().get(0).getConditionalJoin());
        Assert.assertNull("Invalid table condition", selectPlan.getTables().get(1).getConditionalJoin());
        Assert.assertNull("Invalid table condition", selectPlan.getTables().get(2).getConditionalJoin());
    }

    /**
     * Test for SELECT plan performance optimizations with parenthesis.
     *
     * @throws SQLException if has errors.
     */
    @Test
    public void testSelectJoinOptimizationParenthesis() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select distinct 1 from geog.tblAC ac, geog.tblsttes st, geog.County c " +
                        "where c.StateID = st.State and (st.State = ac.State or c.CountyID = 201)");
        final List<StatementNode> list = parser.parse();
        Assert.assertEquals("Invalid list size", 1, list.size());

        final Plan plan = Planner.create(conn, list.get(0));
        Assert.assertTrue("Invalid select plan instance", plan instanceof SelectPlan);

        final SelectPlan selectPlan = (SelectPlan) plan;

        // Remove the conditionals.
        Assert.assertTrue("Invalid join clause", selectPlan.getCondition() instanceof ORNode);

        Assert.assertEquals("Invalid table count", 3, selectPlan.getTables().size());
        Assert.assertNull("Invalid table condition", selectPlan.getTables().get(0).getConditionalJoin());
        Assert.assertNull("Invalid table condition", selectPlan.getTables().get(1).getConditionalJoin());
        Assert.assertTrue("Invalid table condition",
                selectPlan.getTables().get(2).getConditionalJoin() instanceof EqualsNode);
    }

    /**
     * Test for order by.
     *
     * @throws SQLException if has errors.
     */
    @Test
    public void testOrderBy() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select \"DATE\", \"TIME\" from fields.date7 order by \"DATE\", \"TIME\"");
        final List<StatementNode> list = parser.parse();
        Assert.assertEquals("Invalid list size", 1, list.size());

        final Plan plan = Planner.create(conn, list.get(0));
        Assert.assertTrue("Invalid select plan instance", plan instanceof SelectPlan);

        final SelectPlan selectPlan = (SelectPlan) plan;
        final List<Column> columns = selectPlan.getOrderByFields();

        Assert.assertEquals("Invalid column list size", 2, columns.size());
        Assert.assertEquals("Invalid column list size", "DATE", columns.get(0).getName());
        Assert.assertEquals("Invalid column list size", "TIME", columns.get(1).getName());
    }

    /**
     * Test order by in execution.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testOrderByExecution() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select \"DATE\", \"TIME\" from fields.date7 order by \"DATE\", \"TIME\"");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-01", rs.getString("DATE"));
            Assert.assertEquals("Invalid value", "10:00:00", rs.getString("TIME"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-01", rs.getString("DATE"));
            Assert.assertNull("Invalid value", rs.getTime("TIME"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-02", rs.getString("DATE"));
            Assert.assertEquals("Invalid value", "09:25:25", rs.getString("TIME"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-02-01", rs.getString("DATE"));
            Assert.assertEquals("Invalid value", "10:30:00", rs.getString("TIME"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNull("Invalid value", rs.getString("DATE"));
            Assert.assertEquals("Invalid value", "10:00:00", rs.getString("TIME"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test error in date conversion.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testErrorDateConversion() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select a.* from fields.DATE4 a where a.DATE = 1 order by \"DATE\"");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }
}
