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
package com.googlecode.paradox;

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit test for {@link ParadoxResultSet} class.
 *
 * @version 1.7
 * @since 1.3
 */
@SuppressWarnings({"java:S109", "java:S1192"})
public class ParadoxResultSetTest {

    public static final String INVALID_FIRST_STATUS = "Invalid first status";
    public static final String INVALID_RESULT_SET_STATE = "Invalid ResultSet state";
    public static final String INVALID_LAST_STATUS = "Invalid last status";
    public static final String INVALID_ABSOLUTE_VALUE = "Invalid absolute value.";
    public static final String NO_FIRST_ROW = "No first row";
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
    public ParadoxResultSetTest() {
        super();
    }

    /**
     * Register the database driver.
     */
    @BeforeClass
    public static void setUp() {
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
        this.conn = (ParadoxConnection) DriverManager.getConnection(ParadoxResultSetTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with empty values.
     */
    @Test
    public void testAbsoluteEmpty() throws SQLException {
        final List<Column> columns = new ArrayList<>();
        final List<Object[]> values = new ArrayList<>();
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            Assert.assertFalse(INVALID_ABSOLUTE_VALUE, rs.absolute(1));
            Assert.assertTrue(INVALID_ABSOLUTE_VALUE, rs.isAfterLast());
        }
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with high row number.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAbsoluteInvalidRow() throws SQLException {
        final List<Column> columns = new ArrayList<>();
        final List<Object[]> values = new ArrayList<>();
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            Assert.assertFalse(INVALID_ABSOLUTE_VALUE, rs.absolute(-1));
            Assert.assertTrue(INVALID_ABSOLUTE_VALUE, rs.isBeforeFirst());
        }
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with low row number.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAbsoluteLowRowValue() throws SQLException {
        final List<Column> columns = new ArrayList<>();
        final List<Object[]> values = new ArrayList<>();
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            Assert.assertFalse(INVALID_ABSOLUTE_VALUE, rs.absolute(-1));
        }
    }

    /**
     * Test for null number value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNullNumber() throws SQLException {
        final List<Column> columns = Collections.singletonList(new Column("A", ParadoxType.INTEGER));
        final List<Object[]> values = Collections.singletonList(new Object[]{null});
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            Assert.assertTrue("Invalid Result Set state", rs.next());
            Assert.assertEquals("Invalid int value", 0, rs.getInt("A"));
            Assert.assertTrue("Invalid null state", rs.wasNull());
            Assert.assertFalse("Invalid Result Set state", rs.next());
        }
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with negative row
     * value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAbsoluteNegativeRowValue() throws SQLException {
        final List<Column> columns = Collections
                .singletonList(new Column(new ParadoxField(ParadoxType.VARCHAR)));
        final List<Object[]> values = Collections.singletonList(new Object[]{"Test"});
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            Assert.assertTrue(INVALID_ABSOLUTE_VALUE, rs.absolute(1));
            Assert.assertTrue(INVALID_ABSOLUTE_VALUE, rs.absolute(-1));
        }
    }

    /**
     * Test for {@link ParadoxResultSet#afterLast()} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAfterLast() throws SQLException {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        final List<Column> columns = Collections.singletonList(new Column(field));
        final List<Object[]> values = Collections.singletonList(new Object[]{"Test"});
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            rs.afterLast();
            Assert.assertTrue("Testing for invalid position.", rs.isAfterLast());
        }
    }

    /**
     * Test for first result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFirstResult() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as \"ACode\", State, Cities FROM AREACODES")) {
            Assert.assertTrue(NO_FIRST_ROW, rs.next());
            final String firstValue = rs.getString("ACode");
            Assert.assertTrue(NO_FIRST_ROW, rs.next());
            Assert.assertNotEquals("Rows with same value.", firstValue, rs.getString("ACode"));
            Assert.assertTrue("Not in first row", rs.first());
            Assert.assertEquals("Rows with different values", firstValue, rs.getString("ACode"));
        }
    }

    /**
     * Test for is last result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    @SuppressWarnings("java:S2232")
    public void testIsLastResult() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {
            Assert.assertFalse(INVALID_LAST_STATUS, rs.isLast());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertFalse(INVALID_LAST_STATUS, rs.isLast());
            Assert.assertTrue("Invalid after last status", rs.isAfterLast());
            Assert.assertFalse(NO_FIRST_ROW, rs.next());
            rs.absolute(-1);
            Assert.assertTrue(INVALID_LAST_STATUS, rs.isLast());
        }
    }

    /**
     * Test for is first result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testIsFirstResult() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {
            Assert.assertFalse(INVALID_FIRST_STATUS, rs.isFirst());
            Assert.assertTrue(INVALID_FIRST_STATUS, rs.isBeforeFirst());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertTrue(INVALID_FIRST_STATUS, rs.isFirst());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertFalse(INVALID_FIRST_STATUS, rs.isFirst());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertFalse(INVALID_FIRST_STATUS, rs.isFirst());
            Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertFalse(INVALID_FIRST_STATUS, rs.isFirst());
            Assert.assertTrue(INVALID_FIRST_STATUS, rs.first());
            Assert.assertTrue(INVALID_FIRST_STATUS, rs.isFirst());
            rs.beforeFirst();
            Assert.assertTrue(INVALID_FIRST_STATUS, rs.isBeforeFirst());
        }
    }

    /**
     * Test for relative.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    @SuppressWarnings("java:S2232")
    public void testRelative() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {

            rs.relative(10);
            Assert.assertTrue(INVALID_LAST_STATUS, rs.isLast());
            rs.next();
            Assert.assertTrue("Invalid after last status", rs.isAfterLast());
            rs.previous();
            rs.relative(-2);
            rs.relative(-10);
            Assert.assertTrue(INVALID_FIRST_STATUS, rs.isFirst());
        }
    }

    /**
     * Test for get row.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testGetRow() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {

            Assert.assertEquals("Invalid row value", 0, rs.getRow());
            for (int loop = 0; loop <= 3; loop++, rs.next()) {
                Assert.assertEquals("Invalid row value", loop, rs.getRow());
            }

            Assert.assertEquals("Invalid row value", 0, rs.getRow());
        }
    }

    /**
     * Test for fetch direction.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testFetchDirection() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as \"ACode\", State, Cities FROM AREACODES")) {
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            final String firstValue = rs.getString("ACode");
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());

            rs.setFetchDirection(ResultSet.FETCH_REVERSE);
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());

            final String newValue = rs.getString("ACode");
            Assert.assertEquals("Invalid column value", firstValue, newValue);
        }
    }

    /**
     * Test for first result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testNoFirstResult() throws SQLException {
        try (ParadoxStatement statement = (ParadoxStatement) conn.createStatement();
             ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), statement,
                     Collections.emptyList(),
                     Collections.emptyList())) {

            Assert.assertFalse("There is one first row", rs.next());
            Assert.assertFalse("There is one first row", rs.first());
        }
    }

    /**
     * Test for {@link ResultSet} execution.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSet() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as ACode, State, Cities FROM AREACODES")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Testing for column 'AC'.", "201", rs.getString("ACode"));
            Assert.assertEquals("Testing for column 'State'.", "NJ", rs.getString("State"));
            Assert.assertEquals("Testing for column 'Cities'.", "Hackensack, Jersey City (201/551 overlay)",
                    rs.getString("Cities"));
        }
    }

    /**
     * Test for asterisk with alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testAsteriskWithAlias() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.* FROM AREACODES a")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Testing for column 'AC'.", "201", rs.getString("ac"));
            Assert.assertEquals("Testing for column 'State'.", "NJ", rs.getString("State"));
            Assert.assertEquals("Testing for column 'Cities'.", "Hackensack, Jersey City (201/551 overlay)",
                    rs.getString("Cities"));
        }
    }

    /**
     * Test table with schema name.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testTableWithSchemaName() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.* FROM db.AREACODES a")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Testing for column 'AC'.", "201", rs.getString("ac"));
            Assert.assertEquals("Testing for column 'State'.", "NJ", rs.getString("State"));
            Assert.assertEquals("Testing for column 'Cities'.", "Hackensack, Jersey City (201/551 overlay)",
                    rs.getString("Cities"));
        }
    }

    /**
     * Test for cross schema.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCrossSchema() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.* FROM fields.DATE35 a")) {
            Assert.assertTrue("No First row", rs.next());
        }
    }

    /**
     * Test for join and where.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testJoinAndWhere() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("select ac.AreaCode as a, st.State, st.Capital "
                     + " from geog.tblAC ac, geog.tblsttes st" + " where st.State = ac.State")) {
            Assert.assertTrue("No First row", rs.next());
        }
    }

    /**
     * Test for is null.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testIsNull() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt
                     .executeQuery("select \"date\", \"time\" from fields.DATE7 where \"date\" is null")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Invalid time value", "10:00:00", rs.getString("time"));
            Assert.assertFalse("No First row", rs.next());
        }
    }

    /**
     * Test for is not null.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testIsNotNull() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("select \"date\" from fields.DATE7 where \"date\" is not null")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Invalid time.", "2018-01-01", rs.getString("date"));
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Invalid time.", "2018-02-01", rs.getString("date"));
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Invalid time.", "2018-01-02", rs.getString("date"));
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("Invalid time.", "2018-01-01", rs.getString("date"));
            Assert.assertFalse("No First row", rs.next());
        }
    }

    /**
     * Test for convert value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testConvert() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt
                     .executeQuery("select \"date\", \"time\" from fields.DATE7 where \"date\" is null")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertNotNull("Invalid instance", rs.getObject("time", Timestamp.class));
            Assert.assertFalse("No First row", rs.next());
        }
    }

    /**
     * Test for error in conversion value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testErrorInConversion() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.AC FROM db.AREACODES a")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertNull("Error in value conversion", rs.getObject("ac", Timestamp.class));
        }
    }

    /**
     * Test for {@link ResultSet} with multiple values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSetMultipleValues() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT \"ID\", NAME, MONEYS FROM db.GENERAL")) {
            Assert.assertTrue("First record:", rs.next());
            Assert.assertEquals("1 row: ", "1 - Mari 100.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            Assert.assertTrue("Second record:", rs.next());
            Assert.assertEquals("2 row: ", "2 - Katty 150.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            Assert.assertTrue("Third record:", rs.next());
            Assert.assertEquals("2 row: ", "333333333 - Elizabet 75.0",
                    rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
        }
    }

    /**
     * Test {@link ResultSet} with one column.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSetOneColumn() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT EMail FROM CUSTOMER")) {
            Assert.assertTrue("No First row", rs.next());
            Assert.assertEquals("1 row:", "luke@fun.com", rs.getString("email"));
            Assert.assertTrue("No second row", rs.next());
            Assert.assertEquals("2 row:", "fmallory@freeport.org", rs.getString("email"));
            Assert.assertTrue("No third row", rs.next());
            Assert.assertEquals("3 row:", "lpetzold@earthenwear.com", rs.getString("email"));
        }
    }

    /**
     * Test {@link ResultSet} with two columns.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testResultSetTwoColumn() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT EMail,CustNo FROM CUSTOMER")) {
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertEquals("1 row:", "luke@fun.com", rs.getString(1));
            Assert.assertEquals("1 row:", 1, rs.getInt(2));
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertEquals("2 row:", "fmallory@freeport.org", rs.getString("email"));
            Assert.assertEquals("2 row:", 2, rs.getInt("custNo"));
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertEquals("3 row:", "lpetzold@earthenwear.com", rs.getString("Email"));
            Assert.assertEquals("2 row:", 3, rs.getInt("CUSTNO"));
        }
    }

    /**
     * Test for like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testLike() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select ac.AreasCovered from geog.tblAC ac " + " where ac.AreasCovered like 'Hackensack%'")) {

            Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.isAfterLast());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertTrue("Invalid value", rs.getString("AreasCovered").startsWith("Hackensack"));
            Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.next());
        }

        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select ac.AreasCovered from geog.tblAC ac " + " where ac.AreasCovered like 'hackensack%'")) {

            Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.next());
        }
    }

    /**
     * Test for insensitive like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testInsensitiveLike() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select ac.AreasCovered from geog.tblAC ac " + " where ac.AreasCovered ilike 'hackensack%'")) {

            Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.isAfterLast());
            Assert.assertTrue(INVALID_RESULT_SET_STATE, rs.next());
            Assert.assertTrue("Invalid value", rs.getString("AreasCovered").startsWith("Hackensack"));
            Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.next());
        }
    }

    /**
     * Test for execute method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testExecute() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            boolean result = stmt.execute("select \"DECIMAL\" from db.DECIMAL where \"DECIMAL\" = 1");
            Assert.assertTrue("Invalid result set state", result);

            try (final ResultSet rs = stmt.getResultSet()) {
                Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.isAfterLast());
                while (rs.next()) {
                    Assert.assertEquals("Invalid value", 1.0D, rs.getDouble("DECIMAL"), 0.00001D);
                }
            }

            Assert.assertFalse("Invalid result set state", stmt.getMoreResults());
            Assert.assertNull("More result sets", stmt.getResultSet());
        }
    }

    /**
     * Test for execute method in prepared statement.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testExecutePreparedStatement() throws SQLException {
        try (final PreparedStatement stmt = this.conn
                .prepareStatement("select \"DECIMAL\" from db.DECIMAL where \"DECIMAL\" = 1")) {
            boolean result = stmt.execute();
            Assert.assertTrue("Invalid result set state", result);

            try (final ResultSet rs = stmt.getResultSet()) {
                Assert.assertFalse(INVALID_RESULT_SET_STATE, rs.isAfterLast());
                while (rs.next()) {
                    Assert.assertEquals("Invalid value", 1.0D, rs.getDouble("DECIMAL"), 0.00001D);
                }
            }

            Assert.assertFalse("Invalid result set state", stmt.getMoreResults());
            Assert.assertNull("More result sets", stmt.getResultSet());
        }
    }
}
