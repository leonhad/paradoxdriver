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
package com.googlecode.paradox.utils;

/**
 * Stores the driver constants.
 *
 * @version 1.3
 * @since 1.0
 */
public final class Constants {

    /**
     * Driver Name.
     */
    public static final String DRIVER_NAME = "Paradox Driver";
    /**
     * Major version of the Driver.
     */
    public static final int MAJOR_VERSION = 1;
    /**
     * Paradox max string size.
     */
    public static final int MAX_STRING_SIZE = 255;
    /**
     * Minor version of the Driver.
     */
    public static final int MINOR_VERSION = 6;
    /**
     * Driver String Version.
     */
    public static final String DRIVER_VERSION = Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION;
    /**
     * Driver prefix.
     */
    public static final String URL_PREFIX = "jdbc:paradox:";

    /**
     * Max buffer size.
     */
    public static final int MAX_BUFFER_SIZE = 2_048;

    /**
     * Paradox version 4 ID.
     */
    public static final int PARADOX_VERSION_4 = 4;

    /**
     * Default escape char.
     */
    public static final char ESCAPE_CHAR = '\\';

    /**
     * Utility class.
     */
    private Constants() {
        // Utility class.
    }
}
