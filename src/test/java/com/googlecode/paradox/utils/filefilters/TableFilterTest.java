/*
 * TableFilterTest.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.utils.filefilters;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * Unit test for {@link TableFilter}.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public class TableFilterTest {

    /**
     * Test for acceptance.
     */
    @Test
    public void testAccept() {
        final File file = new File("teste.db");
        final TableFilter filter = new TableFilter();
        assertTrue(filter.accept(file));
    }

}