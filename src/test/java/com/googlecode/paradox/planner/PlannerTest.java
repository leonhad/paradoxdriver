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
package com.googlecode.paradox.planner;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.planner.context.SelectContext;
import com.googlecode.paradox.planner.plan.SelectPlan;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link Planner}.
 *
 * @since 1.1
 */
class PlannerTest {

    /**
     * The database test connection.
     */
    private static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/";

    /**
     * The database connection.
     */
    private ParadoxConnection conn;

    /**
     * Creates a new instance.
     */
    public PlannerTest() {
        super();
    }

    /**
     * Register the driver.
     */
    @BeforeAll
    static void setUp() {
        new Driver();
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @AfterEach
    void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @BeforeEach
    void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for a asterisk node plan.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAsterisk() throws SQLException {
        final SQLParser parser = new SQLParser("select * from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        assertNotNull(plan.getColumns());
        assertEquals(3, plan.getColumns().size());
        assertEquals("AC", plan.getColumns().get(0).getName());
        assertEquals("State", plan.getColumns().get(1).getName());
        assertEquals("Cities", plan.getColumns().get(2).getName());
    }

    /**
     * Test for a asterisk node plan.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAsteriskWithTables() throws SQLException {
        final SQLParser parser = new SQLParser("select a.* from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        assertNotNull(plan.getColumns());
        assertEquals(3, plan.getColumns().size());
        assertEquals("AC", plan.getColumns().get(0).getName());
        assertEquals("State", plan.getColumns().get(1).getName());
        assertEquals("Cities", plan.getColumns().get(2).getName());
    }

    /**
     * Test for valid column name.
     *
     * @throws SQLException if there is no errors.
     */
    @Test
    void testColumnName() throws SQLException {
        final SQLParser parser = new SQLParser("select ac from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        assertEquals(1, plan.getColumns().size());
        assertEquals("ac", plan.getColumns().get(0).getName());
    }

    /**
     * Test for a invalid node.
     *
     * @throws SQLException if there is no errors.
     */
    @Test
    void testInvalid() throws SQLException {
        final StatementNode node = new StatementNode("node", null);
        assertThrows(SQLFeatureNotSupportedException.class, () -> Planner.create(conn.getConnectionInfo(), node));
    }

    /**
     * Test for an invalid table.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testInvalidTable() throws SQLException {
        final SQLParser parser = new SQLParser("select * from invalid");
        assertThrows(ParadoxDataException.class, () -> Planner.create(conn.getConnectionInfo(), parser.parse()));
    }

    /**
     * Test for SELECT plan without columns.
     */
    @Test
    void testSelectWithoutColumns()  {
        final SelectNode node = new SelectNode(null);
        assertThrows(ParadoxSyntaxErrorException.class, () -> Planner.create(conn.getConnectionInfo(), node));
    }

    /**
     * Test for SELECT plan with where equals clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSelectWhereEquals() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select ac from areacodes where state = 'NY' and ac = 212 or ac=315 or ac=917");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        assertEquals(3, values.size());
        assertEquals("212", values.get(0)[0]);
        assertEquals("315", values.get(1)[0]);
        assertEquals("917", values.get(2)[0]);
    }

    /**
     * Test for SELECT plan with where notEquals clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSelectWhereNotEquals() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select ac from areacodes where state <> 'NY' and (ac = 212 or ac=315 or ac=917)");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        assertEquals(0, values.size());
    }

    /**
     * Test for SELECT plan with where GreaterThan clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSelectWhereGreaterThan() throws SQLException {
        final SQLParser parser = new SQLParser("select ac from areacodes where state = 'NY' and ac > 845");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        assertEquals(2, values.size());
    }

    /**
     * Test for SELECT plan with where LessThan clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSelectWhereLessThan() throws SQLException {
        final SQLParser parser = new SQLParser("select ac from areacodes where state = 'NY' and ac < 320");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        assertEquals(2, values.size());
    }

    /**
     * Test for SELECT plan with where clause and multiples columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testSelectWhereMultipleColumns() throws SQLException {
        final SQLParser parser = new SQLParser("select * from areacodes where state = 'NY' and ac < 320");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        assertEquals(2, values.size());
        assertEquals("AC", plan.getColumns().get(0).getField().getName());
        assertEquals("State", plan.getColumns().get(1).getField().getName());
        assertEquals("Cities", plan.getColumns().get(2).getField().getName());
    }

    /**
     * Test for SELECT with values in field list.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testValuesInFields() throws SQLException {
        final SQLParser parser = new SQLParser("select 1 as \"1\", 'value' as b, null from areacodes");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        assertEquals(3, plan.getColumns().size());
        assertEquals("1", plan.getColumns().get(0).getValue());
        assertEquals("b", plan.getColumns().get(1).getName());
        assertEquals("value", plan.getColumns().get(1).getValue());
        assertNull(plan.getColumns().get(2).getValue());
    }

    /**
     * Test for group by fields.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupBy() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*) FROM AREACODES group by State");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());

        assertEquals(1, plan.getColumns().stream().filter(c -> !c.isHidden()).count());
        assertNotNull(plan.getColumns().get(0).getFunction());
        assertEquals("count", plan.getColumns().get(0).getFunction().getName());
        assertEquals(1, plan.getGroupBy().getColumns().size());
        assertEquals("State", plan.getGroupBy().getColumns().get(0).getName());
    }

    /**
     * Test for group by fields with an invalid order by field list.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupByWithInvalidOrderBy() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select State, count(*) from geog.tblZCode group by State order by AreaCode");
        final StatementNode tree = parser.parse();
        assertThrows(SQLException.class, () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by with fix values.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupByFixValues() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*), State, 1, 'a' FROM AREACODES group by State, 1, 'a'");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());

        assertEquals(4, plan.getColumns().stream().filter(c -> !c.isHidden()).count());
        assertNotNull(plan.getColumns().get(0).getFunction());
        assertEquals("count", plan.getColumns().get(0).getFunction().getName());
        assertEquals(3, plan.getGroupBy().getColumns().size());
        assertEquals("State", plan.getGroupBy().getColumns().get(0).getName());
        assertEquals("1", plan.getGroupBy().getColumns().get(1).getName());
        assertEquals("a", plan.getGroupBy().getColumns().get(2).getName());
    }

    /**
     * Test for group by and order by with aggregate function.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupByAndOrderWithAggregate() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select count(*), state from geog.tblZCode group by State order by count(*) desc");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());

        assertEquals(2, plan.getColumns().size());
        assertNotNull(plan.getColumns().get(0).getFunction());
        assertEquals("count", plan.getColumns().get(0).getFunction().getName());
        assertEquals(1, plan.getGroupBy().getColumns().size());
        assertEquals("State", plan.getGroupBy().getColumns().get(0).getName());
    }

    /**
     * Test for group by with invalid field list.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupByInvalidFieldList() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*), Cities FROM AREACODES group by State");
        final StatementNode tree = parser.parse();
        assertThrows(SQLException.class, () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by with invalid field list 2.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupByInvalidFieldList2() throws SQLException {
        final SQLParser parser = new SQLParser("select count(State), AC from db.AREACODES");
        final StatementNode tree = parser.parse();
        assertThrows(SQLException.class, () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by with invalid fix values.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupByInvalidFieldListAndFixValues() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*), 1, 'a' FROM AREACODES group by State, 1");
        final StatementNode tree = parser.parse();
        assertThrows(SQLException.class, () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by without aggregate function.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testGroupByWithoutAggregate() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select AreaCode, state from geog.tblZCode group by AreaCode, state")) {
                assertTrue(rs.next());
            }
        }
    }

    /**
     * Test for table not found.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testTableNotFound() throws SQLException {
        final SQLParser parser = new SQLParser("select * from notfound");
        final StatementNode statementNode = parser.parse();
        assertThrows(ParadoxDataException.class, () -> Planner.create(conn.getConnectionInfo(), statementNode));
    }

    /**
     * Test for no table after from.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testNoTableAfterFrom() throws SQLException {
        final SQLParser parser = new SQLParser("select * from");
        assertThrows(SQLException.class, parser::parse);
    }

    /**
     * Test for SELECT with values in ResultSet.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testValuesInResultSet() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            stmt.setMaxRows(1);
            try (ResultSet rs = stmt.executeQuery("select 1 as \"1\", 'value' as b, null from areacodes")) {

                assertTrue(rs.next());

                assertEquals(1, rs.getInt("1"));
                assertEquals("value", rs.getString("b"));
                assertNull(rs.getString("null"));

                assertFalse(rs.next());
            }
        }
    }

    /**
     * Test for IN with values in ResultSet.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    void testIn() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select id from fields.long where id in (1, '2') order by 1")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt("id"));
                assertTrue(rs.next());
                assertEquals(2, rs.getInt("id"));
                assertFalse(rs.next());
            }
        }
    }

    /**
     * Test for boolean in conditionals.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBoolean() throws SQLException {
        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select BOOL from fields.logical where BOOL = 1")) {
                assertTrue(rs.next());
                assertTrue(rs.getBoolean("BOOL"));
            }
        }
    }

    /**
     * Test for limit and offset.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLimitOffset() throws SQLException {
        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select Id from fields.long order by Id limit 2 offset 1")) {
                assertTrue(rs.next());
                assertEquals(2, rs.getInt("Id"));
                assertTrue(rs.next());
                assertEquals(3, rs.getInt("Id"));
                assertFalse(rs.next());
            }
        }
    }

    /**
     * Test the planer execution cache.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testPlannerCache() throws SQLException {
        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select BOOL from fields.logical where BOOL = 1")) {
                assertTrue(rs.next());
                assertTrue(rs.getBoolean("BOOL"));
            }
        }

        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select BOOL from fields.logical where BOOL = 1")) {
                assertTrue(rs.next());
                assertTrue(rs.getBoolean("BOOL"));
            }
        }
    }
}
