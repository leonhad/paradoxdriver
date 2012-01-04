package org.paradox.data;

import java.sql.DriverManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.paradox.Driver;
import org.paradox.ParadoxConnection;
import org.paradox.metadata.ParadoxField;

/**
 *
 * @author 72330554168
 */
public class ViewDataTest {

    @Before
    public void setUp() throws ClassNotFoundException {
        Class.forName(Driver.class.getName());
    }

    @Test
    public void testListViews() throws Exception {
        ParadoxConnection conn = (ParadoxConnection)DriverManager.getConnection("jdbc:paradox:./db");
        ViewData.listViews(conn);
    }

    @Test
    public void testParseExpression() throws Exception {
        final ParadoxField field = new ParadoxField();
        ViewData.parseExpression(field, "_PC, CALC _PC*_QTD AS CUSTOTOTAL");
        Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(field.isChecked()));
        Assert.assertEquals("_PC", field.getJoinName());
        Assert.assertEquals("CALC _PC*_QTD", field.getExpression());
        Assert.assertEquals("CUSTOTOTAL", field.getAlias());

        Assert.assertTrue(field.isChecked());
    }
}