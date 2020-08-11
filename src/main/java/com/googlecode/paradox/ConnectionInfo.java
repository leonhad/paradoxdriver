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

import com.googlecode.paradox.data.filefilters.DirectoryFilter;
import com.googlecode.paradox.exceptions.ParadoxDataException;
import com.googlecode.paradox.exceptions.ParadoxException;
import com.googlecode.paradox.exceptions.ParadoxNotSupportedException;
import com.googlecode.paradox.metadata.Schema;
import com.googlecode.paradox.metadata.schema.DirectorySchema;
import com.googlecode.paradox.metadata.schema.SystemSchema;
import com.googlecode.paradox.utils.Expressions;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores the connection information properties.
 *
 * @version 1.0
 * @since 1.6.0
 */
public final class ConnectionInfo {

    private static final Logger LOGGER = Logger.getLogger(ConnectionInfo.class.getName());

    /**
     * Charset property key.
     */
    public static final String CHARSET_KEY = "charset";

    /**
     * Locale property key.
     */
    public static final String LOCALE_KEY = "locale";

    /**
     * BCD rounding property key.
     */
    public static final String BCD_ROUNDING_KEY = "bcd_rounding";

    /**
     * User property key.
     */
    public static final String USER_KEY = "user";

    /**
     * Timezone property key.
     */
    public static final String TIMEZONE_KEY = "timezone";

    /**
     * Enable catalog property key.
     */
    public static final String ENABLE_CATALOG_KEY = "enable_catalogs";

    /**
     * Default charset value.
     */
    public static final Charset DEFAULT_CHARSET = null;

    /**
     * Default locale value.
     */
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * Default BCD rounding value.
     */
    public static final boolean DEFAULT_BCD_ROUND = true;

    /**
     * Default timezone.
     */
    public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();

    /**
     * Default enable catalog.
     */
    public static final boolean DEFAULT_ENABLE_CATALOG = false;

    /**
     * Default user.
     */
    public static final String DEFAULT_USER = "sys";

    /**
     * Information schema name.
     */
    public static final String INFORMATION_SCHEMA = "information_schema";

    /**
     * Driver URL.
     */
    private final String url;

    /**
     * Default charset.
     */
    private Charset charset = DEFAULT_CHARSET;

    /**
     * Connection locale.
     */
    private Locale locale = DEFAULT_LOCALE;

    /**
     * Time zone.
     */
    private TimeZone timeZone = DEFAULT_TIMEZONE;

    /**
     * BCD field precision.
     */
    private boolean bcdRounding = DEFAULT_BCD_ROUND;

    /**
     * The current connection schema.
     */
    private Schema currentSchema;

    /**
     * The current catalog.
     */
    private File currentCatalog;

    /**
     * This connection holdability.
     */
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;

    /**
     * Enable catalog change.
     */
    private boolean enableCatalogChange = DEFAULT_ENABLE_CATALOG;

    /**
     * Connection user.
     */
    private String user = DEFAULT_USER;

    /**
     * Creates a new instance.
     *
     * @param url the connection url.
     */
    public ConnectionInfo(final String url) {
        this.url = url;
    }

    /**
     * List the connections schema in selected catalog.
     *
     * @param catalog       the database catalog.
     * @param schemaPattern the schema pattern.
     * @return the schema directories.
     * @throws SQLException in case of failures.
     */
    public List<Schema> getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        File[] catalogs = null;

        if (!enableCatalogChange) {
            if (catalog == null || getCatalog().equalsIgnoreCase(catalog)) {
                catalogs = new File[]{
                        currentCatalog
                };
            }
        } else {
            final File parent = currentCatalog.getParentFile();
            if (!parent.isDirectory()) {
                throw new ParadoxDataException(ParadoxDataException.Error.INVALID_CATALOG_PATH);
            }

            catalogs = parent.listFiles(new DirectoryFilter(locale, catalog));
        }

        final List<Schema> ret = new ArrayList<>();
        if (catalogs != null) {
            for (final File catalogFile : catalogs) {
                final File[] schemas = catalogFile.listFiles(new DirectoryFilter(this.locale, schemaPattern));
                if (schemas != null) {
                    ret.addAll(Stream.of(schemas)
                            .filter(Objects::nonNull)
                            .map(DirectorySchema::new)
                            .collect(Collectors.toList()));
                }

                if (schemaPattern == null ||
                        Expressions.accept(locale, INFORMATION_SCHEMA, schemaPattern, false, '\\')) {
                    ret.add(new SystemSchema(this, catalogFile.getName()));
                }
            }

        }

        ret.sort(Comparator.comparing(Schema::name));
        return ret;
    }

    /**
     * List the connections schema in selected catalog.
     *
     * @param catalog    the database catalog.
     * @param schemaName the schema name.
     * @return the schema directories.
     * @throws SQLException in case of failures.
     */
    public Schema getSchema(final String catalog, final String schemaName) throws SQLException {
        File[] catalogs = null;

        if (!enableCatalogChange) {
            if (catalog == null || getCatalog().equalsIgnoreCase(catalog)) {
                catalogs = new File[]{
                        currentCatalog
                };
            }
        } else {
            File parent = currentCatalog.getParentFile();
            if (!parent.isDirectory()) {
                throw new ParadoxDataException(ParadoxDataException.Error.INVALID_CATALOG_PATH);
            }

            catalogs = parent.listFiles(new DirectoryFilter(locale, catalog));
        }

        if (catalogs == null || catalogs.length != 1) {
            throw new ParadoxDataException(ParadoxDataException.Error.INVALID_CATALOG_NAME);
        }

        if (INFORMATION_SCHEMA.equalsIgnoreCase(schemaName)) {
            return new SystemSchema(this, catalogs[0].getName());
        }

        for (final File catalogFile : catalogs) {
            final File[] schemas = catalogFile.listFiles(new DirectoryFilter(this.locale, schemaName));
            if (schemas != null && schemas.length == 1) {
                return new DirectorySchema(schemas[0]);
            }
        }

        throw new ParadoxException(ParadoxException.Error.SCHEMA_NOT_FOUND);
    }

    /**
     * Gets a property value.
     *
     * @param key          the property key.
     * @param defaultValue the default value in case of property not set.
     * @param info         the property information.
     * @return the property value or {@code defaultValue} in case of property not set.
     */
    private static String getPropertyValue(final String key, final String defaultValue, final Properties info) {
        String ret = null;
        if (info != null) {
            ret = info.getProperty(key);
        }

        if (ret == null) {
            ret = defaultValue;
        }

        return ret;
    }

    /**
     * Gets the property metadata.
     *
     * @param info the property information.
     * @return the list of driver property information.
     */
    @SuppressWarnings("i18n-java:V1017")
    public static DriverPropertyInfo[] getMetaData(final Properties info) {
        final String charsetValue = getPropertyValue(CHARSET_KEY, null, info);
        final String localeValue = getPropertyValue(LOCALE_KEY, DEFAULT_LOCALE.toLanguageTag(), info);
        final String bcdRounding = getPropertyValue(BCD_ROUNDING_KEY, String.valueOf(DEFAULT_BCD_ROUND), info);
        final String timeZoneId = getPropertyValue(TIMEZONE_KEY, DEFAULT_TIMEZONE.getID(), info);
        final String enableCatalog = getPropertyValue(ENABLE_CATALOG_KEY, String.valueOf(DEFAULT_ENABLE_CATALOG), info);
        final String user = getPropertyValue(USER_KEY, DEFAULT_USER, info);

        final DriverPropertyInfo bcdRoundingProp = new DriverPropertyInfo(BCD_ROUNDING_KEY, bcdRounding);
        bcdRoundingProp.choices = new String[]{"true", "false"};
        bcdRoundingProp.required = false;
        bcdRoundingProp.description = "Use BCD double rounding (true to use rounding, the original used by Paradox).";

        final DriverPropertyInfo enableCatalogProp = new DriverPropertyInfo(ENABLE_CATALOG_KEY, enableCatalog);
        enableCatalogProp.choices = new String[]{"true", "false"};
        enableCatalogProp.required = false;
        enableCatalogProp.description = "Enable catalog info.";

        final DriverPropertyInfo charset = new DriverPropertyInfo(CHARSET_KEY, charsetValue);
        charset.choices = Charset.availableCharsets().keySet().toArray(new String[0]);
        charset.required = false;
        charset.description = "Table charset (empty value to use the charset defined in table).";
        Arrays.sort(charset.choices);

        final DriverPropertyInfo localeProp = new DriverPropertyInfo(LOCALE_KEY, localeValue);
        localeProp.choices = Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toLanguageTag).toArray(String[]::new);
        localeProp.required = false;
        localeProp.description = "The locale to use internally by the driver.";
        Arrays.sort(localeProp.choices);

        final DriverPropertyInfo timeZoneProp = new DriverPropertyInfo(TIMEZONE_KEY, timeZoneId);
        timeZoneProp.choices = TimeZone.getAvailableIDs();
        timeZoneProp.required = false;
        timeZoneProp.description = "Time zone ID for use in date and time functions.";
        Arrays.sort(timeZoneProp.choices);

        final DriverPropertyInfo userProp = new DriverPropertyInfo(USER_KEY, user);
        userProp.required = false;
        userProp.description = "User to use in connection.";

        final DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", "");
        passwordProp.required = false;
        passwordProp.description = "Password to use in connection.";

        return new DriverPropertyInfo[]{
                bcdRoundingProp,
                charset,
                enableCatalogProp,
                localeProp,
                passwordProp,
                timeZoneProp,
                userProp
        };
    }

    /**
     * Gets a property by its name.
     *
     * @param name the property name.
     * @return the property value.
     */
    public String getProperty(final String name) {
        return getProperties().getProperty(name);
    }

    @SuppressWarnings("java:S2221")
    private static <T> T getProperty(final String name, final String value, final Map<String, ClientInfoStatus> errors,
                                     final T defaultValue, final Function<String, T> converter) {
        try {
            if (value != null && !value.trim().isEmpty()) {
                return converter.apply(value);
            } else {
                return defaultValue;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.FINEST, e.getMessage(), e);
            errors.put(name, ClientInfoStatus.REASON_VALUE_INVALID);
        }

        return defaultValue;
    }

    /**
     * Gets the all properties.
     *
     * @return the all properties.
     */
    public Properties getProperties() {
        final Properties properties = new Properties();

        properties.put(BCD_ROUNDING_KEY, Boolean.toString(bcdRounding));
        if (charset != null) {
            properties.put(CHARSET_KEY, charset.displayName());
        }

        properties.put(DEFAULT_ENABLE_CATALOG, Boolean.toString(enableCatalogChange));
        properties.put(LOCALE_KEY, locale.toLanguageTag());
        properties.put(DEFAULT_TIMEZONE, timeZone.getID());
        properties.put(USER_KEY, user);

        return properties;
    }

    /**
     * Sets the connection properties.
     *
     * @param info the connection property information.
     * @throws SQLClientInfoException in case of invalid property.
     */
    public void setProperties(final Properties info) throws SQLClientInfoException {
        for (Map.Entry<Object, Object> entry : info.entrySet()) {
            final String key = String.valueOf(entry.getKey());
            String value = null;
            if (entry.getValue() != null) {
                value = String.valueOf(entry.getValue());
            }

            put(key, value);
        }
    }

    /**
     * Update a property by its name and value.
     *
     * @param name  the property name.
     * @param value the property value.
     * @throws SQLClientInfoException in case of failures.
     */
    public void put(final String name, final String value) throws SQLClientInfoException {
        final Map<String, ClientInfoStatus> errors = new HashMap<>();
        if (name == null) {
            throw new SQLClientInfoException("Property name can not be null.", errors);
        } else {
            switch (name) {
                case BCD_ROUNDING_KEY:
                    bcdRounding = getProperty(name, value, errors, DEFAULT_BCD_ROUND, Boolean::parseBoolean);
                    break;
                case CHARSET_KEY:
                    charset = getProperty(name, value, errors, DEFAULT_CHARSET, Charset::forName);
                    break;
                case ENABLE_CATALOG_KEY:
                    enableCatalogChange = getProperty(name, value, errors, DEFAULT_ENABLE_CATALOG,
                            Boolean::parseBoolean);
                    break;
                case LOCALE_KEY:
                    locale = getProperty(name, value, errors, DEFAULT_LOCALE, Locale::forLanguageTag);
                    break;
                case TIMEZONE_KEY:
                    timeZone = getProperty(name, value, errors, DEFAULT_TIMEZONE, TimeZone::getTimeZone);
                    break;
                case USER_KEY:
                    user = getProperty(name, value, errors, USER_KEY, String::valueOf);
                    break;
                default:
                    errors.put(name, ClientInfoStatus.REASON_UNKNOWN_PROPERTY);
            }
        }

        if (!errors.isEmpty()) {
            throw new SQLClientInfoException(errors);
        }
    }

    /**
     * Change the current catalog.
     *
     * @param name the catalog name.
     * @throws SQLException in case of invalid catalog.
     */
    public void setCatalog(final String name) throws SQLException {
        if (!enableCatalogChange) {
            if (getCatalog().equalsIgnoreCase(name)) {
                return;
            }

            throw new ParadoxNotSupportedException(ParadoxNotSupportedException.Error.CATALOG_CHANGE);
        }

        final File parent = this.currentCatalog.getParentFile();
        if (!parent.isDirectory()) {
            throw new ParadoxDataException(ParadoxDataException.Error.INVALID_CATALOG_PATH);
        }

        final File newCatalog = new File(parent, name);
        if (!newCatalog.isDirectory()) {
            throw new ParadoxDataException(ParadoxDataException.Error.INVALID_CATALOG_NAME, name);
        }

        this.currentCatalog = newCatalog;
        this.currentSchema = new SystemSchema(this, newCatalog.getName());
    }

    /**
     * Gets the available catalogs.
     *
     * @return the catalog list.
     * @throws ParadoxDataException in case of failures.
     */
    public List<String> listCatalogs() throws ParadoxDataException {
        final List<String> catalogs = new ArrayList<>();
        if (enableCatalogChange && currentCatalog.getParent() != null) {
            final File parent = this.currentCatalog.getParentFile();
            if (!parent.isDirectory()) {
                throw new ParadoxDataException(ParadoxDataException.Error.INVALID_CATALOG_PATH);
            }

            final File[] catalogFiles = parent.listFiles(new DirectoryFilter(locale));
            if (catalogFiles != null) {
                catalogs.addAll(Arrays.stream(catalogFiles)
                        .filter(File::isDirectory)
                        .filter((File catalog) -> {
                            // Not showing catalogs without schemas.
                            final File[] schemas = catalog.listFiles(new DirectoryFilter(locale));
                            return schemas != null && schemas.length > 0;
                        })
                        .map(File::getName)
                        .collect(Collectors.toList())
                );
            }
        } else {
            catalogs.add(getCatalog());
        }

        catalogs.sort(String::compareTo);
        return catalogs;
    }

    /**
     * Gets the URL connection.
     *
     * @return the URL connection
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Gets the default charset.
     *
     * @return the default charset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Sets the default charset.
     *
     * @param charset the default charset.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Gets the connection locale.
     *
     * @return the connection locale.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the connection locale.
     *
     * @param locale the connection locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the BCD rounding.
     *
     * @return <code>true</code> if the BCD rounding is enabled.
     */
    public boolean isBcdRounding() {
        return bcdRounding;
    }

    /**
     * Gets the connection time zone.
     *
     * @return the connection time zone.
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Gets the current schema directory.
     *
     * @return the current schema directory.
     */
    public Schema getCurrentSchema() {
        return this.currentSchema;
    }

    /**
     * Sets the current connection schema.
     *
     * @param schemaName the current connection schema.
     * @throws SQLException in case of schema not found.
     */
    public void setCurrentSchema(final String schemaName) throws SQLException {
        if (INFORMATION_SCHEMA.equalsIgnoreCase(schemaName)) {
            this.currentSchema = new SystemSchema(this, currentCatalog.getName());
        } else {
            final File[] schemas = this.currentCatalog.listFiles(new DirectoryFilter(locale, schemaName));

            if (schemas == null || schemas.length != 1) {
                throw new ParadoxException(ParadoxException.Error.SCHEMA_NOT_FOUND);
            }

            this.currentSchema = new DirectorySchema(schemas[0]);
        }
    }

    /**
     * Sets the current connection schema.
     *
     * @param currentSchema the current connection schema.
     */
    public void setCurrentSchema(final Schema currentSchema) {
        this.currentSchema = currentSchema;
    }

    /**
     * Gets the current catalog.
     *
     * @return the current catalog.
     */
    public String getCatalog() {
        return currentCatalog.getName();
    }

    /**
     * Change the current catalog.
     *
     * @param currentCatalog the current catalog.
     */
    void setCurrentCatalog(final File currentCatalog) {
        this.currentCatalog = currentCatalog;
    }

    /**
     * Gets the default ResultSet holdability.
     *
     * @return the default ResultSet holdability.
     */
    public int getHoldability() {
        return holdability;
    }

    /**
     * Sets the default ResultSet holdability.
     *
     * @param holdability the default ResultSet holdability.
     */
    public void setHoldability(final int holdability) {
        this.holdability = holdability;
    }

    /**
     * Gets the connection user.
     *
     * @return the connection user.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the connection user.
     *
     * @param user the connection user.
     */
    public void setUser(String user) {
        this.user = user;
    }
}
