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
package com.googlecode.paradox;

import com.googlecode.paradox.metadata.paradox.ParadoxField;
import com.googlecode.paradox.results.Column;
import com.googlecode.paradox.results.ParadoxType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link ParadoxResultSet} class.
 *
 * @since 1.3
 */
class ParadoxResultSetTest {

    /**
     * The connection string used in tests.
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
    @BeforeAll
    static void setUp() {
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
    public void connect() throws SQLException {
        this.conn = (ParadoxConnection) DriverManager.getConnection(ParadoxResultSetTest.CONNECTION_STRING + "db");
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with empty values.
     */
    @Test
    void testAbsoluteEmpty() throws SQLException {
        final List<Column> columns = new ArrayList<>();
        final List<Object[]> values = new ArrayList<>();
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            assertFalse(rs.absolute(1));
            assertTrue(rs.isAfterLast());
        }
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with high row number.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAbsoluteInvalidRow() throws SQLException {
        final List<Column> columns = new ArrayList<>();
        final List<Object[]> values = new ArrayList<>();
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            assertFalse(rs.absolute(-1));
            assertTrue(rs.isBeforeFirst());
        }
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with low row number.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAbsoluteLowRowValue() throws SQLException {
        final List<Column> columns = new ArrayList<>();
        final List<Object[]> values = new ArrayList<>();
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            assertFalse(rs.absolute(-1));
        }
    }

    /**
     * Test for null number value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNullNumber() throws SQLException {
        final List<Column> columns = Collections.singletonList(new Column("A", ParadoxType.INTEGER));
        final List<Object[]> values = Collections.singletonList(new Object[]{null});
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt("A"));
            assertTrue(rs.wasNull());
            assertFalse(rs.next());
        }
    }

    /**
     * Test for {@link ParadoxResultSet#absolute(int)} method with negative row
     * value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAbsoluteNegativeRowValue() throws SQLException {
        final List<Column> columns = Collections
                .singletonList(new Column(new ParadoxField(ParadoxType.VARCHAR)));
        final List<Object[]> values = Collections.singletonList(new Object[]{"Test"});
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            assertTrue(rs.absolute(1));
            assertTrue(rs.absolute(-1));
        }
    }

    /**
     * Test for {@link ParadoxResultSet#afterLast()} method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAfterLast() throws SQLException {
        final ParadoxField field = new ParadoxField(ParadoxType.VARCHAR);
        final List<Column> columns = Collections.singletonList(new Column(field));
        final List<Object[]> values = Collections.singletonList(new Object[]{"Test"});
        final ParadoxStatement stmt = (ParadoxStatement) conn.createStatement();
        try (final ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), stmt, values, columns)) {
            rs.afterLast();
            assertTrue(rs.isAfterLast());
        }
    }

    /**
     * Test for first result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFirstResult() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as \"ACode\", State, Cities FROM AREACODES")) {
            assertTrue(rs.next());
            final String firstValue = rs.getString("ACode");
            assertTrue(rs.next());
            assertNotEquals("Rows with same value.", firstValue, rs.getString("ACode"));
            assertTrue(rs.first());
            assertEquals(firstValue, rs.getString("ACode"));
        }
    }

    /**
     * Test for is last result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIsLastResult() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {
            assertFalse(rs.isLast());
            assertTrue(rs.next());
            assertTrue(rs.next());
            assertTrue(rs.next());
            assertFalse(rs.next());
            assertFalse(rs.isLast());
            assertTrue(rs.isAfterLast());
            assertFalse(rs.next());
            rs.absolute(-1);
            assertTrue(rs.isLast());
        }
    }

    /**
     * Test for is first result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIsFirstResult() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {
            assertFalse(rs.isFirst());
            assertTrue(rs.isBeforeFirst());
            assertTrue(rs.next());
            assertTrue(rs.isFirst());
            assertTrue(rs.next());
            assertFalse(rs.isFirst());
            assertTrue(rs.next());
            assertFalse(rs.isFirst());
            assertFalse(rs.next());
            assertFalse(rs.isFirst());
            assertTrue(rs.first());
            assertTrue(rs.isFirst());
            rs.beforeFirst();
            assertTrue(rs.isBeforeFirst());
        }
    }

    /**
     * Test for relative.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testRelative() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {

            rs.relative(10);
            assertTrue(rs.isLast());
            rs.next();
            assertTrue(rs.isAfterLast());
            rs.previous();
            rs.relative(-2);
            rs.relative(-10);
            assertTrue(rs.isFirst());
        }
    }

    /**
     * Test for get row.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testGetRow() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fields.DATE5")) {

            assertEquals(0, rs.getRow());
            for (int loop = 0; loop <= 3; loop++, rs.next()) {
                assertEquals(loop, rs.getRow());
            }

            assertEquals(0, rs.getRow());
        }
    }

    /**
     * Test for fetch direction.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFetchDirection() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as \"ACode\", State, Cities FROM AREACODES")) {
            assertTrue(rs.next());
            final String firstValue = rs.getString("ACode");
            assertTrue(rs.next());

            rs.setFetchDirection(ResultSet.FETCH_REVERSE);
            assertTrue(rs.next());

            final String newValue = rs.getString("ACode");
            assertEquals(firstValue, newValue);
        }
    }

    /**
     * Test for first result.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNoFirstResult() throws SQLException {
        try (ParadoxStatement statement = (ParadoxStatement) conn.createStatement();
             ParadoxResultSet rs = new ParadoxResultSet(this.conn.getConnectionInfo(), statement, Collections.emptyList(), Collections.emptyList())) {

            assertFalse(rs.next());
            assertFalse(rs.first());
        }
    }

    /**
     * Test for {@link ResultSet} execution.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testResultSet() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AC as ACode, State, Cities FROM AREACODES")) {
            assertTrue(rs.next());
            assertEquals("201", rs.getString("ACode"));
            assertEquals("NJ", rs.getString("State"));
            assertEquals("Hackensack, Jersey City (201/551 overlay)", rs.getString("Cities"));
        }
    }

    /**
     * Test for asterisk with alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAsteriskWithAlias() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.* FROM AREACODES a")) {
            assertTrue(rs.next());
            assertEquals("201", rs.getString("ac"));
            assertEquals("NJ", rs.getString("State"));
            assertEquals("Hackensack, Jersey City (201/551 overlay)", rs.getString("Cities"));
        }
    }

    /**
     * Test table with schema name.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTableWithSchemaName() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.* FROM db.AREACODES a")) {
            assertTrue(rs.next());
            assertEquals("201", rs.getString("ac"));
            assertEquals("NJ", rs.getString("State"));
            assertEquals("Hackensack, Jersey City (201/551 overlay)", rs.getString("Cities"));
        }
    }

    /**
     * Test for cross schema.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCrossSchema() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.* FROM fields.DATE35 a")) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for join and where.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testJoinAndWhere() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("select ac.AreaCode as a, st.State, st.Capital "
                     + " from geog.tblAC ac, geog.tblsttes st where st.State = ac.State")) {
            assertTrue(rs.next());
        }
    }

    /**
     * Test for is null.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIsNull() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt
                     .executeQuery("select \"date\", \"time\" from fields.DATE7 where \"date\" is null")) {
            assertTrue(rs.next());
            assertEquals("10:00:00", rs.getString("time"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for is not null.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIsNotNull() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("select \"date\" from fields.DATE7 where \"date\" is not null")) {
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getString("date"));
            assertTrue(rs.next());
            assertEquals("2018-02-01", rs.getString("date"));
            assertTrue(rs.next());
            assertEquals("2018-01-02", rs.getString("date"));
            assertTrue(rs.next());
            assertEquals("2018-01-01", rs.getString("date"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for convert value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testConvert() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt
                     .executeQuery("select \"date\", \"time\" from fields.DATE7 where \"date\" is null")) {
            assertTrue(rs.next());
            assertNotNull(rs.getObject("time", Timestamp.class));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for error in conversion value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testErrorInConversion() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.AC FROM db.AREACODES a")) {
            assertTrue(rs.next());
            assertNull(rs.getObject("ac", Timestamp.class));
        }
    }

    /**
     * Test for {@link ResultSet} with multiple values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testResultSetMultipleValues() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT \"ID\", NAME, MONEYS FROM db.GENERAL")) {
            assertTrue(rs.next());
            assertEquals("1 - Mari 100.0", rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            assertTrue(rs.next());
            assertEquals("2 - Katty 150.0", rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
            assertTrue(rs.next());
            assertEquals("333333333 - Elizabet 75.0", rs.getLong(1) + " - " + rs.getString(2) + " " + rs.getFloat(3));
        }
    }

    /**
     * Test {@link ResultSet} with one column.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testResultSetOneColumn() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT EMail FROM CUSTOMER")) {
            assertTrue(rs.next());
            assertEquals("luke@fun.com", rs.getString("email"));
            assertTrue(rs.next());
            assertEquals("fmallory@freeport.org", rs.getString("email"));
            assertTrue(rs.next());
            assertEquals("lpetzold@earthenwear.com", rs.getString("email"));
        }
    }

    /**
     * Test {@link ResultSet} with two columns.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testResultSetTwoColumn() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT EMail,CustNo FROM CUSTOMER")) {
            assertTrue(rs.next());
            assertEquals("luke@fun.com", rs.getString(1));
            assertEquals(1, rs.getInt(2));
            assertTrue(rs.next());
            assertEquals("fmallory@freeport.org", rs.getString("email"));
            assertEquals(2, rs.getInt("custNo"));
            assertTrue(rs.next());
            assertEquals("lpetzold@earthenwear.com", rs.getString("Email"));
            assertEquals(3, rs.getInt("CUSTNO"));
        }
    }

    /**
     * Test for like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLike() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("select ac.AreasCovered from geog.tblAC ac " + " where ac.AreasCovered like 'Hackensack%'")) {

            assertFalse(rs.isAfterLast());
            assertTrue(rs.next());
            assertTrue(rs.getString("AreasCovered").startsWith("Hackensack"));
            assertFalse(rs.next());
        }

        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("select ac.AreasCovered from geog.tblAC ac " + " where ac.AreasCovered like 'hackensack%'")) {

            assertFalse(rs.next());
        }
    }

    /**
     * Test for insensitive like.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testInsensitiveLike() throws SQLException {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select ac.AreasCovered from geog.tblAC ac " + " where ac.AreasCovered ilike 'hackensack%'")) {

            assertFalse(rs.isAfterLast());
            assertTrue(rs.next());
            assertTrue(rs.getString("AreasCovered").startsWith("Hackensack"));
            assertFalse(rs.next());
        }
    }

    /**
     * Test for execute method.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testExecute() throws SQLException {
        try (Statement stmt = this.conn.createStatement()) {
            boolean result = stmt.execute("select \"DECIMAL\" from db.DECIMAL where \"DECIMAL\" = 1");
            assertTrue(result);

            try (final ResultSet rs = stmt.getResultSet()) {
                assertFalse(rs.isAfterLast());
                while (rs.next()) {
                    assertEquals(1.0D, rs.getDouble("DECIMAL"), 0.00001D);
                }
            }

            assertFalse(stmt.getMoreResults());
            assertNull(stmt.getResultSet());
        }
    }

    /**
     * Test for execute method in prepared statement.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testExecutePreparedStatement() throws SQLException {
        try (final PreparedStatement stmt = this.conn.prepareStatement("select \"DECIMAL\" from db.DECIMAL where \"DECIMAL\" = 1")) {
            boolean result = stmt.execute();
            assertTrue(result);

            try (final ResultSet rs = stmt.getResultSet()) {
                assertFalse(rs.isAfterLast());
                while (rs.next()) {
                    assertEquals(1.0D, rs.getDouble("DECIMAL"), 0.00001D);
                }
            }

            assertFalse(stmt.getMoreResults());
            assertNull(stmt.getResultSet());
        }
    }
}
