package org.paradox.parser;

import java.io.IOException;
import static java.nio.CharBuffer.wrap;
import org.junit.Test;
import static org.junit.Assert.*;

public class ScannerTest {

    /**
     * Test of hasNext method, of class Scanner.
     */
    @Test
    public void testHasNext() throws Exception {
        Scanner scanner = new Scanner(wrap("(SELECT * FROM Teste)"));
        for (int loop = 0; loop < 6; loop++) {
            assertTrue(scanner.hasNext());
            scanner.nextToken();
        }
        assertFalse(scanner.hasNext());
    }

    /**
     * Test of pushBack method, of class Scanner.
     */
    @Test
    public void testPushBack() throws IOException {
        Token token = null;
        Scanner scanner = new Scanner(wrap("(SELECT * FROM Teste)"));
        for (int loop = 0; loop < 6; loop++) {
            token = scanner.nextToken();
        }
        assertFalse(scanner.hasNext());
        assertNotNull(token);
        assertEquals(")", token.getValue());
        scanner.pushBack(token);

        assertTrue(scanner.hasNext());
        token = scanner.nextToken();
        assertFalse(scanner.hasNext());
        assertNotNull(token);
        assertEquals(")", token.getValue());
    }

    @Test
    public void testGroup() throws IOException {
        Token token = null;
        Scanner scanner = new Scanner(wrap(" \"test 1\" \"Table.db\"" ));
        token = scanner.nextToken();
        assertEquals("test 1", token.getValue());
        token = scanner.nextToken();
        assertEquals("Table.db", token.getValue());
    }
}