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
package com.googlecode.paradox.metadata;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ParadoxView} class.
 *
 * @author Leonardo Costa
 * @version 1.2
 * @since 1.3
 */
public class ParadoxViewTest {

    /**
     * Test for field order.
     */
    @Test
    public void testFieldOrder() {
        final short[] list = new short[0];
        final ParadoxView view = new ParadoxView(null, null, null);
        view.setFieldsOrder(list);
        Assert.assertEquals("Invalid value.", list, view.getFieldsOrder());
    }

    /**
     * Test for fields.
     */
    @Test
    public void testFields() {
        final ParadoxField[] list = new ParadoxField[0];
        final ParadoxView view = new ParadoxView(null, null, null);
        view.setFields(list);
        Assert.assertArrayEquals("Invalid value.", list, view.getFields());
    }

    /**
     * Test for field sort.
     */
    @Test
    public void testFieldSort() {
        final ParadoxField[] list = new ParadoxField[0];
        final ParadoxView view = new ParadoxView(null, null, null);
        view.setFieldsSort(list);
        Assert.assertArrayEquals("Invalid value.", list, view.getFieldsSort());
    }
}
