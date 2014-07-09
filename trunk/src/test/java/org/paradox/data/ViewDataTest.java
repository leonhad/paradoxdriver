package org.paradox.data;

import static java.lang.Class.forName;
import static java.sql.DriverManager.getConnection;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.paradox.Driver;
import org.paradox.ParadoxConnection;
import static org.paradox.data.ViewData.listViews;
import static org.paradox.data.ViewData.parseExpression;
import org.paradox.metadata.ParadoxField;

/**
 *
 * @author 72330554168
 */
public class ViewDataTest {

    @Before
    public void setUp() throws ClassNotFoundException {
        forName(Driver.class.getName());
    }

    @Test
    public void testListViews() throws Exception {
        ParadoxConnection conn = (ParadoxConnection)getConnection("jdbc:paradox:./db");
        listViews(conn);
    }

    @Test
    public void testParseExpression() throws Exception {
        final ParadoxField field = new ParadoxField();
        parseExpression(field, "_PC, CALC _PC*_QTD AS CUSTOTOTAL");
        assertEquals(true, field.isChecked());
        assertEquals("_PC", field.getJoinName());
        assertEquals("CALC _PC*_QTD", field.getExpression());
        assertEquals("CUSTOTOTAL", field.getAlias());

        assertTrue(field.isChecked());
    }
}