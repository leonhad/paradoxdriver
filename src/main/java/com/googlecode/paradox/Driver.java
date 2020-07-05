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
package com.googlecode.paradox;

import com.googlecode.paradox.utils.Constants;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PARADOX JDBC Driver type 4.
 *
 * @version 2.3
 * @since 1.0
 */
@SuppressWarnings("squid:S2176")
public final class Driver implements java.sql.Driver {

    public static final String CHARSET_KEY = "charset";

    public static final String LOCALE_KEY = "locale";

    public static final String BCD_ROUNDING_KEY = "bcd_rounding";

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    // Register the drive into JDBC API.
    static {
        try {
            // Register The Paradox Driver
            final Driver driverInst = new Driver();
            DriverManager.registerDriver(driverInst);
        } catch (final SQLException e) {
            Driver.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean acceptsURL(final String url) {
        return (url != null) && url.startsWith(Constants.URL_PREFIX);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (this.acceptsURL(url)) {
            final String dirName = url.substring(Constants.URL_PREFIX.length());
            return new ParadoxConnection(new File(dirName), url, info);
        }
        return null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Logger getParentLogger() {
        return Driver.LOGGER;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) {
        String charsetValue = null;
        String localeValue = null;
        String bcdRounding = null;

        if (info != null) {
            charsetValue = info.getProperty(CHARSET_KEY);
            localeValue = info.getProperty(LOCALE_KEY);
            bcdRounding = info.getProperty(BCD_ROUNDING_KEY);
        }

        if (localeValue == null) {
            localeValue = Locale.ENGLISH.getLanguage();
        }

        if (bcdRounding == null) {
            bcdRounding = "true";
        }

        final DriverPropertyInfo charset = new DriverPropertyInfo(CHARSET_KEY, charsetValue);
        charset.required = false;
        charset.description = "Table charset (empty value to use the charset defined in table).";

        final DriverPropertyInfo localeProp = new DriverPropertyInfo(LOCALE_KEY, localeValue);
        localeProp.required = false;
        localeProp.description = "The locale to use internally by the driver.";

        final DriverPropertyInfo bcdRoundingProp = new DriverPropertyInfo(BCD_ROUNDING_KEY, bcdRounding);
        bcdRoundingProp.required = false;
        bcdRoundingProp.description = "Use BCD double rounding (true to use rounding, the original used by Paradox).";

        return new DriverPropertyInfo[]{charset, localeProp, bcdRoundingProp};
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean jdbcCompliant() {
        return false;
    }
}
