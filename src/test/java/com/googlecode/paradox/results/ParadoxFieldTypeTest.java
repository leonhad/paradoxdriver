package com.googlecode.paradox.results;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Unit test for {@link ParadoxFieldType} class.
 *
 * @author Leonardo Alves da Costa
 * @version 1.0
 * @since 1.3
 */
public class ParadoxFieldTypeTest {

    /**
     * Test for SQL type.
     *
     * @throws SQLException in case of errors.
     */
    @Test
    public void testSQLType() throws SQLException {
        Assert.assertEquals("Test for get SQL type.", ParadoxFieldType.AUTO_INCREMENT.getSQLType(),
                ParadoxFieldType.getSQLType(ParadoxFieldType.AUTO_INCREMENT.getType()));
    }

    /**
     * Test for invalid type.
     *
     * @throws SQLException if there is no errors.
     */
    @Test(expected = SQLException.class)
    public void getType() throws SQLException {
        ParadoxFieldType.getSQLType(-1);
    }

}