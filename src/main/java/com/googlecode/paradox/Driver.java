/*
 * Driver.java
 *
 * 03/14/2009
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
package com.googlecode.paradox;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.paradox.utils.Constants;

/**
 * PARADOX JDBC Driver type 4.
 *
 * @author Leonardo Alves da Costa
 * @version 2.2
 * @since 1.0
 */
public class Driver implements java.sql.Driver {

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    /**
     * Driver properties, if has some.
     */
    private Properties properties = null;

    /**
     * Register the drive into JDBC API.
     */
    static {
        try {
            // Register The Paradox Driver
            final Driver driverInst = new Driver();
            DriverManager.registerDriver(driverInst);
        } catch (final SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return url.startsWith(Constants.URL_PREFIX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (acceptsURL(url)) {
            final String dirName = url.substring(Constants.URL_PREFIX.length(), url.length());
            return new ParadoxConnection(new File(dirName), url, info);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Driver.LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        if (properties == null) {
            properties = new Properties();
        }
        final ArrayList<DriverPropertyInfo> prop = new ArrayList<DriverPropertyInfo>();

        if (info.getProperty("DBNAME") == null) {
            final DriverPropertyInfo dbProp = new DriverPropertyInfo("name", properties.getProperty("name"));
            dbProp.required = false;
            dbProp.description = "Database name";
            prop.add(dbProp);
        }
        if (info.getProperty("password") == null) {
            final DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", "");
            passwordProp.required = false;
            passwordProp.description = "Password to use for authentication";
            prop.add(passwordProp);
        }
        final DriverPropertyInfo dpi[] = new DriverPropertyInfo[prop.size()];
        return prop.toArray(dpi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean jdbcCompliant() {
        return false;
    }
}
