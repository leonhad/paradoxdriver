/*
 * Copyright (c) 2009 Leonardo Alves da Costa
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
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.planner.Planner;
import com.googlecode.paradox.planner.nodes.comparable.EqualsNode;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import org.junit.*;

import java.sql.*;

/**
 * Unit test for {@link SelectPlan} class.
 *
 * @version 1.9
 * @since 1.3
 */
@SuppressWarnings({"java:S109", "java:S1192"})
public class SelectPlanTest {

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
     * Test for invalid column name.
     */
    @Test
    public void testInvalidColumnName() {
        Assert.assertThrows("Invalid column name", ParadoxException.class,
                () -> this.conn.prepareStatement("select invalid from fields.date7"));
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
        final StatementNode tree = parser.parse();

        final Plan<?, ?> plan = Planner.create(conn.getConnectionInfo(), tree);
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
     * Test for SELECT with count optimization.
     *
     * @throws SQLException if has errors.
     */
    @Test
    public void testSelectCountOptimization() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select count(*) from geog.tblAC ac cross join geog.tblsttes st cross join geog.County c");
             final ResultSet rs = stmt.executeQuery()) {

            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 41061680, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for SELECT with count optimization and where.
     *
     * @throws SQLException if has errors.
     */
    @Test
    public void testSelectCountWhereOptimization() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select count(*) from geog.tblAC ac cross join geog.tblsttes st cross join geog.County c " +
                        " where st.State = ac.State and c.StateID = st.State");
             final ResultSet rs = stmt.executeQuery()) {

            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 14722, rs.getInt(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
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
        final StatementNode tree = parser.parse();

        final Plan<?, ?> plan = Planner.create(conn.getConnectionInfo(), tree);
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
        final StatementNode tree = parser.parse();

        final Plan<?, ?> plan = Planner.create(conn.getConnectionInfo(), tree);
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
     * Test order by.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testOrderBy() throws SQLException {
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
     * Test order by with fields not in SELECT expression.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testOrderByNotInSelect() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select \"DATE\" from fields.date7 order by \"TIME\"");
             final ResultSet rs = stmt.executeQuery()) {

            // Test for hidden columns.
            Assert.assertEquals("Invalid column list", 1, rs.getMetaData().getColumnCount());
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-02", rs.getString(1));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-01", rs.getString(1));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNull("Invalid value", rs.getString("DATE"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-02-01", rs.getString(1));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-01", rs.getString(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test NOT statement SELECT expression.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNotExpression() throws SQLException {
        try (final Statement stmt = this.conn.createStatement();
             final ResultSet rs = stmt.executeQuery("select * from joins.joinb where not Id = 2 order by Id")) {

            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 3, rs.getInt("Id"));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 4, rs.getInt("Id"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test NOT statement with parenthesis SELECT expression.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNotWithParenthesis() throws SQLException {
        try (final Statement stmt = this.conn.createStatement();
             final ResultSet rs = stmt.executeQuery(
                     "select * from joins.joinb where not (Id = 2 or Id = 3) order by Id")) {

            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", 4, rs.getInt("Id"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for complex column in functions.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testComplexColumnsAndFunctions() throws SQLException {
        try (final Statement stmt = this.conn.createStatement();
             final ResultSet rs = stmt.executeQuery(
                     "select distinct areaCode " +
                             "from geog.tblAC ac " +
                             "         inner join geog.tblsttes st on st.State = ac.State " +
                             "         inner join geog.County c on c.StateID = st.State " +
                             "where upper(AreasCovered) like upper(trim('hackensack%')) " +
                             "order by \"Admitted Order\" desc")) {

            Assert.assertTrue("Invalid result set state", rs.next());
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

    /**
     * Test for function with fields.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFunctionWithFields() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select upper(Cities) as ret from AREACODES where AC = 202");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "WASHINGTON D.C.", rs.getString("ret"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for function with function alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFunctionWithFunctionAlias() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select DATE(CURRENT_DATE)");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNotNull("Invalid value", rs.getString(1));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFunction() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select upper('upper') as ret");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "UPPER", rs.getString("ret"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for function in clause.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFunctionInClause() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select \"DATE\" from fields.date7 where \"DATE\" = DATE('2018-01-02')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "2018-01-02", rs.getString("DATE"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for recursive function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testRecursiveFunction() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select SUBSTRING(upper('upper'), 1, 2) as ret");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertEquals("Invalid column count", 1, rs.getMetaData().getColumnCount());

            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "UP", rs.getString("ret"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for function alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFunctionAlias() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select upper('upper')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "UPPER", rs.getString("upper('upper')"));
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for order by nulls.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testOrderByNull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac cross join " +
                " geog.tblsttes st cross join geog.County c where st.State = ac.State and c.StateID = st.State " +
                " order by 12 desc, 3 desc");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLike() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac cross join " +
                " geog.tblsttes st cross join geog.County c where st.State = ac.State and c.StateID = st.State and " +
                " AreasCovered like 'hackensack%'");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for upper in like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testUpperInLike() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac cross join " +
                " geog.tblsttes st cross join geog.County c where st.State = ac.State and c.StateID = st.State " +
                " and upper(AreasCovered) like upper('hackensack%')");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for left join with no values in first table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLeftInNoValuesTable() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac " +
                " left join geog.tblsttes st on st.State = ac.State " +
                " left join geog.County c on c.StateID = st.State where AreasCovered like 'hackensack%'");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertFalse("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for left join and full join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLeftAndFull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac " +
                " left join geog.tblsttes st on st.State = ac.State " +
                " left join geog.County c on c.StateID = st.State");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
        }
    }

    /**
     * Test for left join and cross join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLeftAndCross() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac " +
                " left join geog.tblsttes st on st.State = ac.State " +
                " cross join geog.County c where c.StateID = st.State");
             final ResultSet rs = stmt.executeQuery()) {
            Assert.assertTrue("Invalid result set state", rs.next());
        }
    }

    /**
     * Test distinct with hidden columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testDistinctWithHiddenColumns() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "SELECT distinct State FROM AREACODES ORDER BY State, AC");
             final ResultSet rs = stmt.executeQuery()) {

            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertEquals("Invalid value", "--", rs.getString(1));
            Assert.assertTrue("Invalid result set state", rs.next());
            Assert.assertNotEquals("Invalid value", "--", rs.getString(1));
        }
    }
}
