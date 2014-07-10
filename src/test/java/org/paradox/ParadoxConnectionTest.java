package org.paradox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test for Connection class
 *
 * @author Leonardo Alves da Costa
 * @since 10/07/2014
 * @version 1.0
 */
@RunWith(JUnit4.class)
public class ParadoxConnectionTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName("org.paradox.Driver");
    }

    @Test
    public void testInstance() throws Exception {
        exception.expect(SQLException.class);
        DriverManager.getConnection("jdbc:paradox:");
    }
    
    @Test
    public void testDirectory() throws Exception {
        exception.expect(SQLException.class);
        DriverManager.getConnection("jdbc:paradox:invalid");
    }
    
}
