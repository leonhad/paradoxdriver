/*
 * ScannerTest.java 03/12/2009 Copyright (C) 2009 Leonardo Alves da Costa This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser;

import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Scanner}.
 *
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public class ScannerTest {
    
    /**
     * Test for character values.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testCharacterValues() throws Exception {
        final Scanner scanner = new Scanner(" 'test 1' ");
        final Token token = scanner.nextToken();
        Assert.assertEquals(TokenType.CHARACTER, token.getType());
        Assert.assertEquals("test 1", token.getValue());
        Assert.assertFalse(scanner.hasNext());
    }
    
    /**
     * Test for groups.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testGroup() throws Exception {
        final Scanner scanner = new Scanner(" \"test 1\" \"Table.db \"\" \" ");
        Token token = scanner.nextToken();
        Assert.assertEquals("test 1", token.getValue());
        token = scanner.nextToken();
        Assert.assertEquals("Table.db \" ", token.getValue());
    }
    
    /**
     * Test of hasNext method, of class Scanner.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testHasNext() throws Exception {
        final Scanner scanner = new Scanner("(SELECT * FROM Test) ");
        for (int loop = 0; loop < 6; loop++) {
            Assert.assertTrue(scanner.hasNext());
            scanner.nextToken();
        }
        Assert.assertFalse(scanner.hasNext());
    }
    
    /**
     * Test for NULL token.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testNull() throws Exception {
        final Scanner scanner = new Scanner(" NULL");
        final Token token = scanner.nextToken();
        Assert.assertEquals(TokenType.NULL, token.getType());
        Assert.assertFalse(scanner.hasNext());
    }
    
    /**
     * Test for two dots in number.
     *
     * @throws Exception
     *             if test succeed.
     */
    @Test(expected = SQLException.class)
    public void testNumericTwoDots() throws Exception {
        final Scanner scanner = new Scanner("123.8.7");
        scanner.nextToken();
    }
    
    /**
     * Test for numeric values.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testNumericValues() throws Exception {
        final Scanner scanner = new Scanner(" 123 123.8 ");
        Token token = scanner.nextToken();
        Assert.assertEquals(TokenType.NUMERIC, token.getType());
        Assert.assertEquals("123", token.getValue());
        Assert.assertTrue(scanner.hasNext());
        token = scanner.nextToken();
        Assert.assertEquals(TokenType.NUMERIC, token.getType());
        Assert.assertEquals("123.8", token.getValue());
        Assert.assertFalse(scanner.hasNext());
    }
    
    /**
     * Test of pushBack method, of class Scanner.
     *
     * @throws Exception
     *             in case of failures.
     */
    @Test
    public void testPushBack() throws Exception {
        Token token = null;
        final Scanner scanner = new Scanner("(SELECT * from Test) ");
        while (scanner.hasNext()) {
            token = scanner.nextToken();
        }
        Assert.assertFalse(scanner.hasNext());
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.RPAREN, token.getType());
        Assert.assertEquals(")", token.getValue());
        scanner.pushBack(token);
        
        Assert.assertTrue(scanner.hasNext());
        token = scanner.nextToken();
        Assert.assertFalse(scanner.hasNext());
        Assert.assertNotNull(token);
        Assert.assertEquals(TokenType.RPAREN, token.getType());
        Assert.assertEquals(")", token.getValue());
    }
}
