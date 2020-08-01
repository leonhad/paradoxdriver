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

package com.googlecode.paradox.function.system;

import com.googlecode.paradox.utils.Constants;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link VersionFunction}.
 *
 * @version 1.0
 * @since 1.6.0
 */
public class VersionFunctionTest {

    /**
     * Test for version function.
     */
    @Test
    public void testVersion() {
        final String system = String.format("%s %s (%s), %s %s (%s)",
                System.getProperty("java.vm.name"), System.getProperty("java.runtime.version"),
                System.getProperty("java.vendor.version"), System.getProperty("os.name"),
                System.getProperty("os.version"), System.getProperty("os.arch"));

        final VersionFunction versionFunction = new VersionFunction();
        Assert.assertEquals("Invalid version", Constants.DRIVER_NAME + " " + Constants.DRIVER_VERSION
                + " on " + system,
                versionFunction.execute(null, null, null, null));
    }
}