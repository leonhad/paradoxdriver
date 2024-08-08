/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.data.filefilters;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link TableFilter}.
 *
 * @since 1.0
 */
class TableFilterTest {

    /**
     * Test for acceptance.
     */
    @Test
    void testAccept() {
        final File file = new File(this.getClass().getResource("/fields/DATE4.db").getFile());
        final TableFilter filter = new TableFilter(Locale.ENGLISH);
        assertTrue(filter.accept(file));
    }

}
