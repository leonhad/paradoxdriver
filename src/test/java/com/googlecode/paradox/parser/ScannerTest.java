package com.googlecode.paradox.parser;

import java.io.IOException;
import java.nio.CharBuffer;

import org.junit.Assert;
import org.junit.Test;

public class ScannerTest {

	/**
	 * Test of hasNext method, of class Scanner.
	 */
	@Test
	public void testHasNext() throws Exception {
		final Scanner scanner = new Scanner(CharBuffer.wrap("(SELECT * FROM Teste)"));
		for (int loop = 0; loop < 6; loop++) {
			Assert.assertTrue(scanner.hasNext());
			scanner.nextToken();
		}
		Assert.assertFalse(scanner.hasNext());
	}

	/**
	 * Test of pushBack method, of class Scanner.
	 */
	@Test
	public void testPushBack() throws IOException {
		Token token = null;
		final Scanner scanner = new Scanner(CharBuffer.wrap("(SELECT * from Teste)"));
		while (scanner.hasNext()) {
			token = scanner.nextToken();
		}
		Assert.assertFalse(scanner.hasNext());
		Assert.assertNotNull(token);
		Assert.assertEquals(")", token.getValue());
		scanner.pushBack(token);

		Assert.assertTrue(scanner.hasNext());
		token = scanner.nextToken();
		Assert.assertFalse(scanner.hasNext());
		Assert.assertNotNull(token);
		Assert.assertEquals(")", token.getValue());
	}

	@Test
	public void testCharValues() throws IOException {
		Token token = null;
		final Scanner scanner = new Scanner(CharBuffer.wrap(" 'test 1' 'Table.db '' '"));
		token = scanner.nextToken();
		Assert.assertEquals("test 1", token.getValue());
		token = scanner.nextToken();
		Assert.assertEquals("Table.db ' ", token.getValue());
	}

	@Test
	public void testGroup() throws IOException {
		Token token = null;
		final Scanner scanner = new Scanner(CharBuffer.wrap(" \"test 1\" \"Table.db \"\" \" "));
		token = scanner.nextToken();
		Assert.assertEquals("test 1", token.getValue());
		token = scanner.nextToken();
		Assert.assertEquals("Table.db \" ", token.getValue());
	}
}