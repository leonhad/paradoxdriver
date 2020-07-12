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
package com.googlecode.paradox.parser;

import com.googlecode.paradox.Driver;
import com.googlecode.paradox.ParadoxConnection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit test for {@link Scanner}.
 *
 * @version 1.4
 * @since 1.0
 */
public class ScannerTest {

    /**
     * The connection string used in this tests.
     */
    public static final String CONNECTION_STRING = "jdbc:paradox:target/test-classes/db";

    private static ParadoxConnection conn;

    /**
     * Register the database driver.
     *
     * @throws SQLException in case of failures.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        new Driver();
        conn = (ParadoxConnection) DriverManager.getConnection(CONNECTION_STRING);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test for character values.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testCharacterValues() throws Exception {
        final Scanner scanner = new Scanner(conn, " 'test 1' ");
        final Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.CHARACTER, token.getType());
        Assert.assertEquals("Invalid token value.", "test 1", token.getValue());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for groups.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testGroup() throws Exception {
        final Scanner scanner = new Scanner(conn, " \"test 1\" \"Table.db \"\" \" ");
        Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token value.", "test 1", token.getValue());
        token = scanner.nextToken();
        Assert.assertEquals("Invalid token value.", "Table.db \" ", token.getValue());
    }

    /**
     * Test of hasNext method, of class Scanner.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testHasNext() throws Exception {
        final Scanner scanner = new Scanner(conn, "(SELECT * FROM Test) ");

        Assert.assertTrue("Invalid scanner state", scanner.hasNext());
        Assert.assertEquals("Invalid token type", TokenType.L_PAREN, scanner.nextToken().getType());
        Assert.assertTrue("Invalid scanner state", scanner.hasNext());
        Assert.assertEquals("Invalid token type", TokenType.SELECT, scanner.nextToken().getType());
        Assert.assertTrue("Invalid scanner state", scanner.hasNext());
        Assert.assertEquals("Invalid token type", TokenType.ASTERISK, scanner.nextToken().getType());
        Assert.assertTrue("Invalid scanner state", scanner.hasNext());
        Assert.assertEquals("Invalid token type", TokenType.FROM, scanner.nextToken().getType());
        Assert.assertTrue("Invalid scanner state", scanner.hasNext());
        Assert.assertEquals("Invalid token type", TokenType.IDENTIFIER, scanner.nextToken().getType());
        Assert.assertTrue("Invalid scanner state", scanner.hasNext());
        Assert.assertEquals("Invalid token type", TokenType.R_PAREN, scanner.nextToken().getType());
        Assert.assertFalse("Invalid scanner state", scanner.hasNext());
    }

    /**
     * Test for NULL token.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testNull() throws Exception {
        final Scanner scanner = new Scanner(conn, " NULL");
        final Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.NULL, token.getType());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for comments.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testComments() throws Exception {
        final Scanner scanner = new Scanner(conn, " -- Commented line\nNULL");
        final Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.NULL, token.getType());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for multiline comments.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testMultilineComments() throws Exception {
        final Scanner scanner = new Scanner(conn, " /* Commented\n\nline */\nNULL");
        final Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.NULL, token.getType());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for two dots in number.
     *
     * @throws Exception if test succeed.
     */
    @Test(expected = SQLException.class)
    public void testNumericTwoDots() throws Exception {
        final Scanner scanner = new Scanner(conn, "123.8.7");
        scanner.nextToken();
    }

    /**
     * Test for numeric values.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testNumericValues() throws Exception {
        final Scanner scanner = new Scanner(conn, " 123 123.8 ");
        Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.NUMERIC, token.getType());
        Assert.assertEquals("Invalid token value.", "123", token.getValue());
        Assert.assertTrue("Invalid scanner state.", scanner.hasNext());
        token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.NUMERIC, token.getType());
        Assert.assertEquals("Invalid token value.", "123.8", token.getValue());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for negative numbers.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testNegativeNumbers() throws Exception {
        final Scanner scanner = new Scanner(conn, "-123.2");
        final Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.NUMERIC, token.getType());
        Assert.assertEquals("Invalid token value.", "-123.2", token.getValue());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for numeric validation.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testNumericValidation() throws Exception {
        final Scanner scanner = new Scanner(conn, "12a");
        Assert.assertEquals("Invalid value.", "12a", scanner.nextToken().getValue());
    }

    /**
     * Test for is null tokens.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testIsNull() throws Exception {
        final Scanner scanner = new Scanner(conn, "is null");
        Assert.assertEquals("Invalid token type.", TokenType.IS, scanner.nextToken().getType());
        Assert.assertEquals("Invalid token type.", TokenType.NULL, scanner.nextToken().getType());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for minus sign.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testMinusSign() throws Exception {
        final Scanner scanner = new Scanner(conn, "- 123.2");
        Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.MINUS, token.getType());
        token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.NUMERIC, token.getType());
        Assert.assertEquals("Invalid token value.", "123.2", token.getValue());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test for question mark token.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testQuestionMark() throws SQLException {
        final Scanner scanner = new Scanner(conn, "?");
        Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type.", TokenType.QUESTION_MARK, token.getType());
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
    }

    /**
     * Test of pushBack method, of class Scanner.
     *
     * @throws Exception in case of failures.
     */
    @Test
    public void testPushBack() throws Exception {
        Token token = null;
        final Scanner scanner = new Scanner(conn, "(SELECT * from Test) ");
        while (scanner.hasNext()) {
            token = scanner.nextToken();
        }
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
        Assert.assertNotNull("Invalid token value.", token);
        Assert.assertEquals("Invalid token type.", TokenType.R_PAREN, token.getType());
        Assert.assertEquals("Invalid token value.", ")", token.getValue());
        scanner.pushBack(token);

        Assert.assertTrue("Invalid scanner state.", scanner.hasNext());
        token = scanner.nextToken();
        Assert.assertFalse("Invalid scanner state.", scanner.hasNext());
        Assert.assertNotNull("Invalid token value.", token);
        Assert.assertEquals("Invalid token type.", TokenType.R_PAREN, token.getType());
        Assert.assertEquals("Invalid token value.", ")", token.getValue());
    }

    /**
     * Test for character escapes.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testCharacterEscapes() throws SQLException {
        final Scanner scanner = new Scanner(conn, "'\\n\\b\\r\\\\\\t\\'");
        Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type", TokenType.CHARACTER, token.getType());
        Assert.assertEquals("Invalid token value", "\n\b\r\\\t\\", token.getValue());
        Assert.assertFalse("Invalid scanner state", scanner.hasNext());
    }

    /**
     * Test for quoted string.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testQuoted() throws SQLException {
        final Scanner scanner = new Scanner(conn, "'a''b'");
        Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type", TokenType.CHARACTER, token.getType());
        Assert.assertEquals("Invalid token value", "a'b", token.getValue());
        Assert.assertFalse("Invalid scanner state", scanner.hasNext());
    }

    /**
     * Test for double quoted string.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    public void testDoubleQuoted() throws SQLException {
        final Scanner scanner = new Scanner(conn, "\"a\"\"b");
        Token token = scanner.nextToken();
        Assert.assertEquals("Invalid token type", TokenType.IDENTIFIER, token.getType());
        Assert.assertEquals("Invalid token value", "a\"b", token.getValue());
        Assert.assertFalse("Invalid scanner state", scanner.hasNext());
    }
}
