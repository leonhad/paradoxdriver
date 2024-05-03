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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link SelectPlan} class.
 *
 * @since 1.3
 */
class SelectPlanTest {

    /**
     * The connection string used in  tests.
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
    @BeforeAll
    static void initClass() {
        new Driver();
    }

    /**
     * Close the test connection.
     *
     * @throws SQLException in case of failures.
     */
    @AfterEach
    void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to the test database.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeEach
    void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(SelectPlanTest.CONNECTION_STRING + "db");
    }

    /**
     * Test DISTINCT.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDistinct() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select distinct REQTYPE from server");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("G", rs.getString("REQTYPE"));
            assertTrue(rs.next());
            assertEquals("P", rs.getString("REQTYPE"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for invalid column name.
     */
    @Test
    void testInvalidColumnName() {
        assertThrows(ParadoxException.class, () -> this.conn.prepareStatement("select invalid from fields.date7"));
    }

    /**
     * Test for SELECT plan performance optimizations.
     *
     * @throws SQLException if has errors.
     */
    @Test
    void testSelectJoinOptimization() throws SQLException {
        final SQLParser parser = new SQLParser("select distinct 1 from geog.tblAC ac, geog.tblsttes st, geog.County c" +
                " where c.StateID = st.State and st.State = ac.State and c.CountyID = 201");
        final StatementNode tree = parser.parse();

        final Plan<?, ?> plan = Planner.create(conn.getConnectionInfo(), tree);
        assertInstanceOf(SelectPlan.class, plan);

        final SelectPlan selectPlan = (SelectPlan) plan;

        // Remove the conditionals.
        assertNull(selectPlan.getCondition());

        assertEquals(3, selectPlan.getTables().size());
        assertInstanceOf(EqualsNode.class, selectPlan.getTables().get(1).getConditionalJoin());
        assertInstanceOf(ANDNode.class, selectPlan.getTables().get(2).getConditionalJoin());
    }

    /**
     * Test for SELECT with count optimization.
     *
     * @throws SQLException if it has errors.
     */
    @Test
    void testSelectCountOptimization() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select count(*) from geog.tblAC ac cross join geog.tblsttes st cross join geog.County c");
             final ResultSet rs = stmt.executeQuery()) {

            assertTrue(rs.next());
            assertEquals(41061680, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for SELECT with count optimization and where.
     *
     * @throws SQLException if it has errors.
     */
    @Test
    void testSelectCountWhereOptimization() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select count(*) from geog.tblAC ac cross join geog.tblsttes st cross join geog.County c " +
                        " where st.State = ac.State and c.StateID = st.State");
             final ResultSet rs = stmt.executeQuery()) {

            assertTrue(rs.next());
            assertEquals(14722, rs.getInt(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for SELECT plan performance optimizations with OR.
     *
     * @throws SQLException if has errors.
     */
    @Test
    void testSelectJoinOptimizationOr() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select distinct 1 from geog.tblAC ac, geog.tblsttes st, geog.County c " +
                        "where c.StateID = st.State and st.State = ac.State or c.CountyID = 201");
        final StatementNode tree = parser.parse();

        final Plan<?, ?> plan = Planner.create(conn.getConnectionInfo(), tree);
        assertInstanceOf(SelectPlan.class, plan);

        final SelectPlan selectPlan = (SelectPlan) plan;

        // Remove the conditionals.
        assertNotNull(selectPlan.getCondition());

        assertEquals(3, selectPlan.getTables().size());
        assertNull(selectPlan.getTables().get(0).getConditionalJoin());
        assertNull(selectPlan.getTables().get(1).getConditionalJoin());
        assertNull(selectPlan.getTables().get(2).getConditionalJoin());
    }

    /**
     * Test for SELECT plan performance optimizations with parenthesis.
     *
     * @throws SQLException if has errors.
     */
    @Test
    void testSelectJoinOptimizationParenthesis() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select distinct 1 from geog.tblAC ac, geog.tblsttes st, geog.County c " +
                        "where c.StateID = st.State and (st.State = ac.State or c.CountyID = 201)");
        final StatementNode tree = parser.parse();

        final Plan<?, ?> plan = Planner.create(conn.getConnectionInfo(), tree);
        assertInstanceOf(SelectPlan.class, plan);

        final SelectPlan selectPlan = (SelectPlan) plan;

        // Remove the conditionals.
        assertInstanceOf(ORNode.class, selectPlan.getCondition());

        assertEquals(3, selectPlan.getTables().size());
        assertNull(selectPlan.getTables().get(0).getConditionalJoin());
        assertNull(selectPlan.getTables().get(1).getConditionalJoin());
        assertInstanceOf(EqualsNode.class, selectPlan.getTables().get(2).getConditionalJoin());
    }

    /**
     * Test order by.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testOrderBy() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select \"DATE\", \"TIME\" from fields.date7 order by \"DATE\", \"TIME\"");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getString("DATE"));
            assertEquals("10:00:00", rs.getString("TIME"));
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getString("DATE"));
            assertNull(rs.getTime("TIME"));
            assertTrue(rs.next());
            assertEquals("2018-01-02", rs.getString("DATE"));
            assertEquals("09:25:25", rs.getString("TIME"));
            assertTrue(rs.next());
            assertEquals("2018-02-01", rs.getString("DATE"));
            assertEquals("10:30:00", rs.getString("TIME"));
            assertTrue(rs.next());
            assertNull(rs.getString("DATE"));
            assertEquals("10:00:00", rs.getString("TIME"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test order by with fields not in SELECT expression.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testOrderByNotInSelect() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select \"DATE\" from fields.date7 order by \"TIME\"");
             final ResultSet rs = stmt.executeQuery()) {

            // Test for hidden columns.
            assertEquals(1, rs.getMetaData().getColumnCount());
            assertTrue(rs.next());
            assertEquals("2018-01-02", rs.getString(1));
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getString(1));
            assertTrue(rs.next());
            assertNull(rs.getString("DATE"));
            assertTrue(rs.next());
            assertEquals("2018-02-01", rs.getString(1));
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test NOT statement SELECT expression.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNotExpression() throws SQLException {
        try (final Statement stmt = this.conn.createStatement();
             final ResultSet rs = stmt.executeQuery("select * from joins.joinb where not Id = 2 order by Id")) {

            assertTrue(rs.next());
            assertEquals(3, rs.getInt("Id"));
            assertTrue(rs.next());
            assertEquals(4, rs.getInt("Id"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test NOT statement with parenthesis SELECT expression.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNotWithParenthesis() throws SQLException {
        try (final Statement stmt = this.conn.createStatement();
             final ResultSet rs = stmt.executeQuery("select * from joins.joinb where not (Id = 2 or Id = 3) order by Id")) {

            assertTrue(rs.next());
            assertEquals(4, rs.getInt("Id"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for complex column in functions.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testComplexColumnsAndFunctions() throws SQLException {
        try (final Statement stmt = this.conn.createStatement();
             final ResultSet rs = stmt.executeQuery("select distinct areaCode " +
                     "from geog.tblAC ac " +
                     "         inner join geog.tblsttes st on st.State = ac.State " +
                     "         inner join geog.County c on c.StateID = st.State " +
                     "where upper(AreasCovered) like upper(trim('hackensack%')) " +
                     "order by \"Admitted Order\" desc")) {

            assertTrue(rs.next());
        }
    }

    /**
     * Test error in date conversion.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testErrorDateConversion() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select a.* from fields.DATE4 a where a.DATE = 1 order by \"DATE\"");
             final ResultSet rs = stmt.executeQuery()) {
            assertFalse(rs.next());
        }
    }

    /**
     * Test for function with fields.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionWithFields() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select upper(Cities) as ret from AREACODES where AC = 202");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("WASHINGTON D.C.", rs.getString("ret"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for function with function alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionWithFunctionAlias() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select DATE(CURRENT_DATE)");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertNotNull(rs.getString(1));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunction() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select upper('upper') as ret");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("UPPER", rs.getString("ret"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for function in clause.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionInClause() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement(
                "select \"DATE\" from fields.date7 where \"DATE\" = DATE('2018-01-02')");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("2018-01-02", rs.getString("DATE"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for recursive function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testRecursiveFunction() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select SUBSTRING(upper('upper'), 1, 2) as ret");
             final ResultSet rs = stmt.executeQuery()) {
            assertEquals(1, rs.getMetaData().getColumnCount());

            assertTrue(rs.next());
            assertEquals("UP", rs.getString("ret"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for function alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionAlias() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select upper('upper')");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("UPPER", rs.getString("upper('upper')"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for order by nulls.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testOrderByNull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac cross join " +
                " geog.tblsttes st cross join geog.County c where st.State = ac.State and c.StateID = st.State " +
                " order by 12 desc, 3 desc");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLike() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac cross join " +
                " geog.tblsttes st cross join geog.County c where st.State = ac.State and c.StateID = st.State and " +
                " AreasCovered like 'hackensack%'");
             final ResultSet rs = stmt.executeQuery()) {
            assertFalse(rs.next());
        }
    }

    /**
     * Test for upper in like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testUpperInLike() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac cross join " +
                " geog.tblsttes st cross join geog.County c where st.State = ac.State and c.StateID = st.State " +
                " and upper(AreasCovered) like upper('hackensack%')");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for left join with no values in first table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLeftInNoValuesTable() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac " +
                " left join geog.tblsttes st on st.State = ac.State " +
                " left join geog.County c on c.StateID = st.State where AreasCovered like 'hackensack%'");
             final ResultSet rs = stmt.executeQuery()) {
            assertFalse(rs.next());
        }
    }

    /**
     * Test for left join and full join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLeftAndFull() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac " +
                " left join geog.tblsttes st on st.State = ac.State " +
                " left join geog.County c on c.StateID = st.State");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for left join and cross join.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLeftAndCross() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select * from geog.tblAC ac " +
                " left join geog.tblsttes st on st.State = ac.State " +
                " cross join geog.County c where c.StateID = st.State");
             final ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test distinct with hidden columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testDistinctWithHiddenColumns() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("SELECT distinct State FROM AREACODES ORDER BY State, AC");
             final ResultSet rs = stmt.executeQuery()) {

            assertTrue(rs.next());
            assertEquals("--", rs.getString(1));
            assertTrue(rs.next());
            assertNotEquals("--", rs.getString(1));
        }
    }
}
