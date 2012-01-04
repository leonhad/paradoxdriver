package org.paradox;

import org.paradox.utils.Constants;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Driver JDBC tipo 4 para o PARADOX
 * 
 * @author Leonardo Alves da Costa
 * @version 2.0
 * @since 14/03/2009
 */
public class Driver implements java.sql.Driver {

    private Properties _Props = null;

    static {
        try {
            // Registra o driver
            final Driver driverInst = new Driver();
            DriverManager.registerDriver(driverInst);
        } catch (Exception e) {
        }
    }

    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        ParadoxConnection conn = null;

        if (acceptsURL(url)) {
            final String dirName = url.substring(Constants.URL_PREFIX.length(), url.length());
            conn = new ParadoxConnection(new File(dirName), url);
        }
        return conn;
    }

    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return url.startsWith(Constants.URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        if (_Props == null) {
            _Props = new Properties();
        }

        DriverPropertyInfo HostProp =
                new DriverPropertyInfo("HOST", _Props.getProperty("HOST"));
        HostProp.required = true;

        HostProp.description = "Hostname of MySQL Server";

        DriverPropertyInfo PortProp =
                new DriverPropertyInfo("PORT", _Props.getProperty("PORT", "3306"));
        PortProp.required = false;
        PortProp.description = "Port number of MySQL Server";

        DriverPropertyInfo DBProp =
                new DriverPropertyInfo("DBNAME", _Props.getProperty("DBNAME"));
        DBProp.required = false;
        DBProp.description = "Database name";

        DriverPropertyInfo UserProp =
                new DriverPropertyInfo("user", "SYSTEM");
        UserProp.required = true;
        UserProp.description = "Username to authenticate as";

        DriverPropertyInfo PasswordProp =
                new DriverPropertyInfo("password", "");
        PasswordProp.required = true;
        PasswordProp.description = "Password to use for authentication";

        DriverPropertyInfo Dpi[] = {HostProp,
            PortProp,
            DBProp,
            UserProp,
            PasswordProp};
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
}
