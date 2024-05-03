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
package com.googlecode.paradox.parser;

import com.googlecode.paradox.exceptions.ParadoxSyntaxErrorException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link Scanner}.
 *
 * @since 1.0
 */
class ScannerTest {

    /**
     * Test for character values.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testCharacterValues() throws Exception {
        final Scanner scanner = new Scanner(" 'test 1' ");
        final Token token = scanner.nextToken();
        assertEquals(TokenType.CHARACTER, token.getType());
        assertEquals("test 1", token.getValue());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for unterminated string.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testUnterminatedString() throws SQLException {
        final Scanner scanner = new Scanner(" 'test 1");
        assertThrows(ParadoxSyntaxErrorException.class, scanner::nextToken);
    }

    /**
     * Test for groups.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testGroup() throws Exception {
        final Scanner scanner = new Scanner(" \"test 1\" \"Table.db \"\" \" ");
        Token token = scanner.nextToken();
        assertEquals("test 1", token.getValue());
        token = scanner.nextToken();
        assertEquals("Table.db \" ", token.getValue());
    }

    /**
     * Test of hasNext method, of class Scanner.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testHasNext() throws Exception {
        final Scanner scanner = new Scanner("(SELECT * FROM Test) ");

        assertTrue(scanner.hasNext());
        assertEquals(TokenType.L_PAREN, scanner.nextToken().getType());
        assertTrue(scanner.hasNext());
        assertEquals(TokenType.SELECT, scanner.nextToken().getType());
        assertTrue(scanner.hasNext());
        assertEquals(TokenType.ASTERISK, scanner.nextToken().getType());
        assertTrue(scanner.hasNext());
        assertEquals(TokenType.FROM, scanner.nextToken().getType());
        assertTrue(scanner.hasNext());
        assertEquals(TokenType.IDENTIFIER, scanner.nextToken().getType());
        assertTrue(scanner.hasNext());
        assertEquals(TokenType.R_PAREN, scanner.nextToken().getType());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for NULL token.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testNull() throws Exception {
        final Scanner scanner = new Scanner(" NULL");
        final Token token = scanner.nextToken();
        assertEquals(TokenType.NULL, token.getType());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for comments.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testComments() throws Exception {
        final Scanner scanner = new Scanner(" -- Commented line\nNULL");
        final Token token = scanner.nextToken();
        assertEquals(TokenType.NULL, token.getType());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for multiline comments.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testMultilineComments() throws Exception {
        final Scanner scanner = new Scanner(" /* Commented\n\nline */\nNULL");
        final Token token = scanner.nextToken();
        assertEquals(TokenType.NULL, token.getType());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for two dots in number.
     *
     * @throws Exception if test succeed.
     */
    @Test
    void testNumericTwoDots() throws Exception {
        final Scanner scanner = new Scanner("123.8.7");
        assertThrows(SQLException.class, scanner::nextToken);
    }

    /**
     * Test for numeric values.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testNumericValues() throws Exception {
        final Scanner scanner = new Scanner(" 123 123.8 ");
        Token token = scanner.nextToken();
        assertEquals(TokenType.NUMERIC, token.getType());
        assertEquals("123", token.getValue());
        assertTrue(scanner.hasNext());
        token = scanner.nextToken();
        assertEquals(TokenType.NUMERIC, token.getType());
        assertEquals("123.8", token.getValue());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for negative numbers.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testNegativeNumbers() throws Exception {
        final Scanner scanner = new Scanner("-123.2");
        final Token token = scanner.nextToken();
        assertEquals(TokenType.NUMERIC, token.getType());
        assertEquals("-123.2", token.getValue());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for numeric validation.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testNumericValidation() throws Exception {
        final Scanner scanner = new Scanner("12a");
        assertEquals("12a", scanner.nextToken().getValue());
    }

    /**
     * Test for is null tokens.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testIsNull() throws Exception {
        final Scanner scanner = new Scanner("is null");
        assertEquals(TokenType.IS, scanner.nextToken().getType());
        assertEquals(TokenType.NULL, scanner.nextToken().getType());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for minus sign.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testMinusSign() throws Exception {
        final Scanner scanner = new Scanner("- 123.2");
        Token token = scanner.nextToken();
        assertEquals(TokenType.MINUS, token.getType());
        token = scanner.nextToken();
        assertEquals(TokenType.NUMERIC, token.getType());
        assertEquals("123.2", token.getValue());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for question mark token.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testQuestionMark() throws SQLException {
        final Scanner scanner = new Scanner("?");
        Token token = scanner.nextToken();
        assertEquals(TokenType.QUESTION_MARK, token.getType());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test of pushBack method, of class Scanner.
     *
     * @throws Exception in case of failures.
     */
    @Test
    void testPushBack() throws Exception {
        Token token = null;
        final Scanner scanner = new Scanner("(SELECT * from Test) ");
        while (scanner.hasNext()) {
            token = scanner.nextToken();
        }
        assertFalse(scanner.hasNext());
        assertNotNull(token);
        assertEquals(TokenType.R_PAREN, token.getType());
        assertEquals(")", token.getValue());
        scanner.pushBack(token);

        assertTrue(scanner.hasNext());
        token = scanner.nextToken();
        assertFalse(scanner.hasNext());
        assertNotNull(token);
        assertEquals(TokenType.R_PAREN, token.getType());
        assertEquals(")", token.getValue());
    }

    /**
     * Test for character escapes.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testCharacterEscapes() throws SQLException {
        final Scanner scanner = new Scanner("'\\n\\b\\r\\\\\\t\\'");
        Token token = scanner.nextToken();
        assertEquals(TokenType.CHARACTER, token.getType());
        assertEquals("\n\b\r\\\t\\", token.getValue());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for quoted string.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testQuoted() throws SQLException {
        final Scanner scanner = new Scanner("'a''b'");
        Token token = scanner.nextToken();
        assertEquals(TokenType.CHARACTER, token.getType());
        assertEquals("a'b", token.getValue());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for double-quoted string.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testDoubleQuoted() throws SQLException {
        final Scanner scanner = new Scanner("\"a\"\"b\"");
        Token token = scanner.nextToken();
        assertEquals(TokenType.IDENTIFIER, token.getType());
        assertEquals("a\"b", token.getValue());
        assertFalse(scanner.hasNext());
    }

    /**
     * Test for escape token.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testEscape() throws SQLException {
        final Scanner scanner = new Scanner("ESCAPE");
        Token token = scanner.nextToken();
        assertEquals(TokenType.ESCAPE, token.getType());
        assertFalse(scanner.hasNext());
    }
}
