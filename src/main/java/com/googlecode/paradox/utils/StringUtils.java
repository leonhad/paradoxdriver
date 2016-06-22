package com.googlecode.paradox.utils;

/**
 * Set string's utilities.
 *
 * @author Andre
 * @since 09/12/2014.
 * @version 1.1
 */
public final class StringUtils {

    private StringUtils() {
        // Not for use.
    }

    public static String removeDb(final String name) {
        return removeSuffix(name, "DB");
    }

    public static String removeMb(final String name) {
        return removeSuffix(name, "MB");
    }

    private static String removeSuffix(String name, final String suffix) {
        if (name != null && name.toUpperCase().endsWith("." + suffix.toUpperCase())) {
            name = name.substring(0, name.length() - 3);
        }
        return name;
    }
}
