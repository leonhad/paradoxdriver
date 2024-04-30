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
import com.googlecode.paradox.parser.SQLParser;
import com.googlecode.paradox.parser.nodes.SelectNode;
import com.googlecode.paradox.parser.nodes.StatementNode;
import com.googlecode.paradox.planner.context.SelectContext;
import com.googlecode.paradox.planner.plan.SelectPlan;
import org.junit.*;

import java.sql.*;
import java.util.List;

/**
 * Unit test for {@link Planner}.
 *
 * @version 1.12
 * @since 1.1
 */
@SuppressWarnings({"java:S2115", "java:S1192", "java:S109"})
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
     * Creates a new instance.
     */
    public PlannerTest() {
        super();
    }

    /**
     * Register the driver.
     */
    @BeforeClass
    public static void setUp() {
        new Driver();
    }

    /**
     * Used to close the test connection.
     *
     * @throws SQLException in case closing of errors.
     */
    @After
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * Connect to test database.
     *
     * @throws SQLException in case of connection errors.
     */
    @Before
    public void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING + "db");
    }

    /**
     * Test for a asterisk node plan.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAsterisk() throws SQLException {
        final SQLParser parser = new SQLParser("select * from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        Assert.assertNotNull("No columns.", plan.getColumns());
        Assert.assertEquals("Number of columns in table.", 3, plan.getColumns().size());
        Assert.assertEquals("First column not 'AC'.", "AC", plan.getColumns().get(0).getName());
        Assert.assertEquals("Second column not 'State'.", "State", plan.getColumns().get(1).getName());
        Assert.assertEquals("Third column not 'Cities'.", "Cities", plan.getColumns().get(2).getName());
    }

    /**
     * Test for a asterisk node plan.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAsteriskWithTables() throws SQLException {
        final SQLParser parser = new SQLParser("select a.* from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
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
        final SQLParser parser = new SQLParser("select ac from areacodes a");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        Assert.assertEquals("Test the column size.", 1, plan.getColumns().size());
        Assert.assertEquals("Test the column name.", "ac", plan.getColumns().get(0).getName());
    }

    /**
     * Test for a invalid node.
     *
     * @throws SQLException if there is no errors.
     */
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testInvalid() throws SQLException {
        final StatementNode node = new StatementNode("node", null);
        Planner.create(conn.getConnectionInfo(), node);
    }

    /**
     * Test for an invalid table.
     *
     * @throws SQLException in case of failures.
     */
    @Test(expected = ParadoxDataException.class)
    public void testInvalidTable() throws SQLException {
        final SQLParser parser = new SQLParser("select * from invalid");
        Planner.create(conn.getConnectionInfo(), parser.parse());
    }

    /**
     * Test for SELECT plan without columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test(expected = SQLException.class)
    public void testSelectWithoutColumns() throws SQLException {
        final SelectNode node = new SelectNode(null);
        Planner.create(conn.getConnectionInfo(), node);
    }

    /**
     * Test for SELECT plan with where equals clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereEquals() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select ac from areacodes where state = 'NY' and ac = 212 or ac=315 or ac=917");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        Assert.assertEquals("Test the result size.", 3, values.size());
        Assert.assertEquals("Test the result value.", "212", values.get(0)[0]);
        Assert.assertEquals("Test the result value.", "315", values.get(1)[0]);
        Assert.assertEquals("Test the result value.", "917", values.get(2)[0]);
    }

    /**
     * Test for SELECT plan with where notEquals clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereNotEquals() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select ac from areacodes where state <> 'NY' and (ac = 212 or ac=315 or ac=917)");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        Assert.assertEquals("Test the result size.", 0, values.size());
    }

    /**
     * Test for SELECT plan with where GreaterThan clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereGreaterThan() throws SQLException {
        final SQLParser parser = new SQLParser("select ac from areacodes where state = 'NY' and ac > 845");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        Assert.assertEquals("Test the result size.", 2, values.size());
    }

    /**
     * Test for SELECT plan with where LessThan clause.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereLessThan() throws SQLException {
        final SQLParser parser = new SQLParser("select ac from areacodes where state = 'NY' and ac < 320");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        Assert.assertEquals("Test the result size.", 2, values.size());
    }

    /**
     * Test for SELECT plan with where clause and multiples columns.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSelectWhereMultipleColumns() throws SQLException {
        final SQLParser parser = new SQLParser("select * from areacodes where state = 'NY' and ac < 320");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        final SelectContext context = plan.createContext(conn.getConnectionInfo(), null, null);
        final List<Object[]> values = plan.execute(context);
        Assert.assertEquals("Test the result size.", 2, values.size());
        Assert.assertEquals("Field expected", "AC", plan.getColumns().get(0).getField().getName());
        Assert.assertEquals("Field expected", "State", plan.getColumns().get(1).getField().getName());
        Assert.assertEquals("Field expected", "Cities", plan.getColumns().get(2).getField().getName());
    }

    /**
     * Test for SELECT with values in field list.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testValuesInFields() throws SQLException {
        final SQLParser parser = new SQLParser("select 1 as \"1\", 'value' as b, null from areacodes");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());
        Assert.assertEquals("Invalid column size", 3, plan.getColumns().size());
        Assert.assertEquals("Field expected", "1", plan.getColumns().get(0).getValue());
        Assert.assertEquals("Field expected", "b", plan.getColumns().get(1).getName());
        Assert.assertEquals("Field expected", "value", plan.getColumns().get(1).getValue());
        Assert.assertNull("Field expected", plan.getColumns().get(2).getValue());
    }

    /**
     * Test for group by fields.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupBy() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*) FROM AREACODES group by State");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());

        Assert.assertEquals("Invalid column size", 1,
                plan.getColumns().stream().filter(c -> !c.isHidden()).count());
        Assert.assertNotNull("Invalid function node", plan.getColumns().get(0).getFunction());
        Assert.assertEquals("Invalid function name", "count", plan.getColumns().get(0).getFunction().getName());
        Assert.assertEquals("Invalid group by field size", 1, plan.getGroupBy().getColumns().size());
        Assert.assertEquals("Invalid group by field name", "State", plan.getGroupBy().getColumns().get(0).getName());
    }

    /**
     * Test for group by fields with an invalid order by field list.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupByWithInvalidOrderBy() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select State, count(*) from geog.tblZCode group by State order by AreaCode");
        final StatementNode tree = parser.parse();
        Assert.assertThrows("Invalid planer value", SQLException.class,
                () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by with fix values.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupByFixValues() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*), State, 1, 'a' FROM AREACODES group by State, 1, 'a'");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());

        Assert.assertEquals("Invalid column size", 4,
                plan.getColumns().stream().filter(c -> !c.isHidden()).count());
        Assert.assertNotNull("Invalid function node", plan.getColumns().get(0).getFunction());
        Assert.assertEquals("Invalid function name", "count", plan.getColumns().get(0).getFunction().getName());
        Assert.assertEquals("Invalid group by field size", 3, plan.getGroupBy().getColumns().size());
        Assert.assertEquals("Invalid group by field name", "State", plan.getGroupBy().getColumns().get(0).getName());
        Assert.assertEquals("Invalid group by field name", "1", plan.getGroupBy().getColumns().get(1).getName());
        Assert.assertEquals("Invalid group by field name", "a", plan.getGroupBy().getColumns().get(2).getName());
    }

    /**
     * Test for group by and order by with aggregate function.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupByAndOrderWithAggregate() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select count(*), state from geog.tblZCode group by State order by count(*) desc");
        final SelectPlan plan = (SelectPlan) Planner.create(conn.getConnectionInfo(), parser.parse());

        Assert.assertEquals("Invalid column size", 2, plan.getColumns().size());
        Assert.assertNotNull("Invalid function node", plan.getColumns().get(0).getFunction());
        Assert.assertEquals("Invalid function name", "count", plan.getColumns().get(0).getFunction().getName());
        Assert.assertEquals("Invalid group by field size", 1, plan.getGroupBy().getColumns().size());
        Assert.assertEquals("Invalid group by field name", "State", plan.getGroupBy().getColumns().get(0).getName());
    }

    /**
     * Test for group by with invalid field list.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupByInvalidFieldList() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*), Cities FROM AREACODES group by State");
        final StatementNode tree = parser.parse();
        Assert.assertThrows("Invalid planer value", SQLException.class,
                () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by with invalid field list 2.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupByInvalidFieldList2() throws SQLException {
        final SQLParser parser = new SQLParser("select count(State), AC from db.AREACODES");
        final StatementNode tree = parser.parse();
        Assert.assertThrows("Invalid planer value", SQLException.class,
                () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by with invalid fix values.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupByInvalidFieldListAndFixValues() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*), 1, 'a' FROM AREACODES group by State, 1");
        final StatementNode tree = parser.parse();
        Assert.assertThrows("Invalid planer value", SQLException.class,
                () -> Planner.create(conn.getConnectionInfo(), tree));
    }

    /**
     * Test for group by without aggregate function.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testGroupByWithoutAggregate() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select AreaCode, state from geog.tblZCode group by AreaCode, " +
                    "state")) {
                Assert.assertTrue("Invalid result set", rs.next());
            }
        }
    }

    /**
     * Test for table not found.
     *
     * @throws SQLException in case of errors.
     */
    @Test(expected = ParadoxDataException.class)
    public void testTableNotFound() throws SQLException {
        final SQLParser parser = new SQLParser("select * from notfound");
        final StatementNode statementNode = parser.parse();
        Planner.create(conn.getConnectionInfo(), statementNode);
    }

    /**
     * Test for no table after from.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testNoTableAfterFrom() throws SQLException {
        final SQLParser parser = new SQLParser("select * from");
        Assert.assertThrows("Invalid table loaded", SQLException.class, parser::parse);
    }

    /**
     * Test for SELECT with values in ResultSet.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testValuesInResultSet() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            stmt.setMaxRows(1);
            try (ResultSet rs = stmt.executeQuery("select 1 as \"1\", 'value' as b, null from areacodes")) {

                Assert.assertTrue("Invalid result set", rs.next());

                Assert.assertEquals("Invalid ResultSet value", 1, rs.getInt("1"));
                Assert.assertEquals("Invalid ResultSet value", "value", rs.getString("b"));
                Assert.assertNull("Invalid ResultSet value", rs.getString("null"));

                Assert.assertFalse("Invalid result set", rs.next());
            }
        }
    }

    /**
     * Test for IN with values in ResultSet.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testIn() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select id from fields.long where id in (1, '2') order by 1")) {
                Assert.assertTrue("Invalid result set", rs.next());
                Assert.assertEquals("Invalid ResultSet value", 1, rs.getInt("id"));
                Assert.assertTrue("Invalid result set", rs.next());
                Assert.assertEquals("Invalid ResultSet value", 2, rs.getInt("id"));
                Assert.assertFalse("Invalid result set", rs.next());
            }
        }
    }

    /**
     * Test for boolean in conditionals.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testBoolean() throws SQLException {
        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select BOOL from fields.logical where BOOL = 1")) {
                Assert.assertTrue("Invalid result set", rs.next());
                Assert.assertTrue("Invalid ResultSet value", rs.getBoolean("BOOL"));
            }
        }
    }

    /**
     * Test for limit and offset.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLimitOffset() throws SQLException {
        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select Id from fields.long order by Id limit 2 offset 1")) {
                Assert.assertTrue("Invalid result set state", rs.next());
                Assert.assertEquals("Invalid value", 2, rs.getInt("Id"));
                Assert.assertTrue("Invalid result set state", rs.next());
                Assert.assertEquals("Invalid value", 3, rs.getInt("Id"));
                Assert.assertFalse("Invalid result set state", rs.next());
            }
        }
    }

    /**
     * Test the planer execution cache.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testPlannerCache() throws SQLException {
        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select BOOL from fields.logical where BOOL = 1")) {
                Assert.assertTrue("Invalid result set", rs.next());
                Assert.assertTrue("Invalid ResultSet value", rs.getBoolean("BOOL"));
            }
        }

        try (final Statement stmt = this.conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select BOOL from fields.logical where BOOL = 1")) {
                Assert.assertTrue("Invalid result set", rs.next());
                Assert.assertTrue("Invalid ResultSet value", rs.getBoolean("BOOL"));
            }
        }
    }
}
