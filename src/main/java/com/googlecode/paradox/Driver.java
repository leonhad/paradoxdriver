package com.googlecode.paradox;

import static java.sql.DriverManager.registerDriver;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import com.googlecode.paradox.utils.Constants;

/**
 * Driver JDBC tipo 4 para o PARADOX
 *
 * @author Leonardo Alves da Costa
 * @version 2.1 
 * @since 14/03/2009
 */
public class Driver implements java.sql.Driver {

    private Properties properties = null;
    
    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    static {
        try {
            // Register The Paradox Driver
            final Driver driverInst = new Driver();
            registerDriver(driverInst);
        } catch (SQLException e) {
            Logger.getLogger(Driver.class.getName()).severe(e.getMessage());
        }
    }

    // FIXME tratar as propriedades
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (acceptsURL(url)) {
            final String dirName = url.substring(Constants.URL_PREFIX.length(), url.length());
            return new ParadoxConnection(new File(dirName), url);
        }
        return null;
    }

    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return url.startsWith(Constants.URL_PREFIX);
    }

    // FIXME tratar as propriedades
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        if (properties == null) {
            properties = new Properties();
        }

        DriverPropertyInfo DBProp = new DriverPropertyInfo("DBNAME", properties.getProperty("DBNAME"));
        DBProp.required = false;
        DBProp.description = "Database name";

        DriverPropertyInfo PasswordProp = new DriverPropertyInfo("password", "");
        PasswordProp.required = false;
        PasswordProp.description = "Password to use for authentication";

        final DriverPropertyInfo Dpi[] = {DBProp, PasswordProp};
        return Dpi;
    }

    @Override
    public int getMajorVersion() {
        return Constants.MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return Constants.MINOR_VERSION;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return LOGGER;
    }
}
