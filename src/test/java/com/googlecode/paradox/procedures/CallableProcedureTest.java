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
package com.googlecode.paradox.procedures;

import com.googlecode.paradox.metadata.ParadoxField;
import com.googlecode.paradox.procedures.math.Average;
import java.sql.DatabaseMetaData;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link AbstractCallableProcedure}.
 *
 * @author Leonardo Alves da Costa
 * @since 1.3
 * @version 1.0
 */
public class CallableProcedureTest {
    
    /**
     * The object to test.
     */
    private final AbstractCallableProcedure call = new Average();
    
    /**
     * Test for default field.
     */
    @Test
    public void testDefaultField() {
        final List<ParadoxField> fields = this.call.getCols();
        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("field", fields.get(0).getName());
        Assert.assertEquals(0xC, fields.get(0).getType());
    }
    
    /**
     * Test for return type procedure.
     */
    @Test
    public void testReturnType() {
        Assert.assertEquals(DatabaseMetaData.procedureReturnsResult, this.call.getReturnType());
    }
}
