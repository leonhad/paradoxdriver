/*
 * Constants.java
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
package com.googlecode.paradox.utils;

/**
 * Stores the driver constants.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.0
 */
public final class Constants {

    /**
     * Driver Name
     */
    public static final String DRIVER_NAME = "Paradox (OpenParadox)";
    /**
     * Driver String Version
     */
    public static final String DRIVER_VERSION = Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION;
    /**
     * Major version of the Driver
     */
    public static final int MAJOR_VERSION = 1;
    /**
     * Paradox max string size
     */
    public static final int MAX_STRING_SIZE = 255;
    /**
     * Minor version of the Driver
     */
    public static final int MINOR_VERSION = 1;
    /**
     * Driver prefix
     */
    public static final String URL_PREFIX = "jdbc:paradox:";
}
