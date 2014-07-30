package com.googlecode.paradox.parser;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

public class ScannerTest {

	/**
	 * Test of hasNext method, of class Scanner.
	 */
	@Test
	public void testHasNext() throws Exception {
		final Scanner scanner = new Scanner("(SELECT * FROM Teste) ");
		for (int loop = 0; loop < 6; loop++) {
			Assert.assertTrue(scanner.hasNext());
			scanner.nextToken();
		}
		Assert.assertFalse(scanner.hasNext());
	}

	/**
	 * Test of pushBack method, of class Scanner.
	 *
	 * @throws SQLException
	 */
	@Test
	public void testPushBack() throws Exception {
		Token token = null;
		final Scanner scanner = new Scanner("(SELECT * from Teste) ");
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

	@Test
	public void testNull() throws Exception {
		final Scanner scanner = new Scanner(" NULL");
		final Token token = scanner.nextToken();
		Assert.assertEquals(TokenType.NULL, token.getType());
		Assert.assertFalse(scanner.hasNext());
	}

	@Test
	public void testCharacterValues() throws Exception {
		final Scanner scanner = new Scanner(" 'test 1' ");
		final Token token = scanner.nextToken();
		Assert.assertEquals(TokenType.CHARACTER, token.getType());
		Assert.assertEquals("test 1", token.getValue());
		Assert.assertFalse(scanner.hasNext());
	}

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

	@Test(expected = SQLException.class)
	public void testNumericTwoDots() throws Exception {
		final Scanner scanner = new Scanner("123.8.7");
		scanner.nextToken();
	}

	@Test
	public void testGroup() throws Exception {
		final Scanner scanner = new Scanner(" \"test 1\" \"Table.db \"\" \" ");
		Token token = scanner.nextToken();
		Assert.assertEquals("test 1", token.getValue());
		token = scanner.nextToken();
		Assert.assertEquals("Table.db \" ", token.getValue());
	}
}