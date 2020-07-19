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
package com.googlecode.paradox.rowset;

import com.googlecode.paradox.exceptions.ParadoxDataException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom values conversion utility class.
 *
 * @version 1.3
 * @since 1.6.0
 */
public final class ValuesConverter {

    private static final Logger LOGGER = Logger.getLogger(ValuesConverter.class.getName());

    /**
     * Default class mapping.
     */
    private static final Map<Class<?>, Function<Object, Object>> CLASS_MAPPING = new HashMap<>();

    /**
     * Utility class, not for use.
     */
    private ValuesConverter() {
        // Not used..
    }

    static {
        CLASS_MAPPING.put(BigDecimal.class, ValuesConverter::getBigDecimal);
        CLASS_MAPPING.put(Boolean.class, ValuesConverter::getBoolean);
        CLASS_MAPPING.put(Byte.class, ValuesConverter::getByte);
        CLASS_MAPPING.put(byte[].class, ValuesConverter::getByteArray);
        CLASS_MAPPING.put(Date.class, ValuesConverter::getDate);
        CLASS_MAPPING.put(Double.class, ValuesConverter::getDouble);
        CLASS_MAPPING.put(Float.class, ValuesConverter::getFloat);
        CLASS_MAPPING.put(Integer.class, ValuesConverter::getInteger);
        CLASS_MAPPING.put(Long.class, ValuesConverter::getLong);
        CLASS_MAPPING.put(Short.class, ValuesConverter::getShort);
        CLASS_MAPPING.put(String.class, ValuesConverter::getString);
        CLASS_MAPPING.put(Time.class, ValuesConverter::getTime);
        CLASS_MAPPING.put(Timestamp.class, ValuesConverter::getTimestamp);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(final Object value, Class<T> type) throws SQLException {
        try {
            return (T) CLASS_MAPPING.get(type).apply(value);
        } catch (final IllegalArgumentException e) {
            throw new ParadoxDataException(ParadoxDataException.Error.INVALID_CONVERSION, e, value);
        }
    }

    public static Boolean getBoolean(final Object value) {
        Boolean ret = null;
        if (value instanceof Boolean) {
            ret = (Boolean) value;
        } else if (value instanceof Number) {
            if (((Number) value).intValue() == 0) {
                ret = Boolean.FALSE;
            } else {
                ret = Boolean.TRUE;
            }
        } else if (value != null) {
            ret = Boolean.valueOf(value.toString());
        }

        return ret;
    }

    public static Byte getByte(final Object value) {
        Byte ret = null;
        if (value instanceof Byte) {
            ret = (Byte) value;
        } else if (value instanceof Number) {
            ret = ((Number) value).byteValue();
        } else if (value != null) {
            try {
                ret = Byte.valueOf(value.toString());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        return ret;
    }

    public static Short getShort(final Object value) {
        Short ret = null;
        if (value instanceof Short) {
            ret = (Short) value;
        } else if (value instanceof Number) {
            ret = ((Number) value).shortValue();
        } else if (value != null) {
            try {
                ret = Short.valueOf(value.toString());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        return ret;
    }

    public static Integer getInteger(final Object value) {
        Integer ret = null;
        if (value instanceof Integer) {
            ret = (Integer) value;
        } else if (value instanceof Number) {
            ret = ((Number) value).intValue();
        } else if (value != null) {
            try {
                ret = Integer.valueOf(value.toString());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        return ret;
    }

    public static Long getLong(final Object value) {
        Long ret = null;
        if (value instanceof Long) {
            ret = (Long) value;
        } else if (value instanceof Number) {
            ret = ((Number) value).longValue();
        } else if (value != null) {
            try {
                ret = Long.valueOf(value.toString());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        return ret;
    }

    public static BigDecimal getBigDecimal(final Object value) {
        BigDecimal ret = null;
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            ret = BigDecimal.valueOf(((Number) value).doubleValue());
        } else if (value != null) {
            try {
                ret = new BigDecimal(value.toString());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        return ret;
    }

    public static Float getFloat(final Object value) {
        Float ret = null;
        if (value instanceof Float) {
            ret = (Float) value;
        } else if (value instanceof Number) {
            ret = ((Number) value).floatValue();
        } else if (value != null) {
            try {
                ret = Float.valueOf(value.toString());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        return ret;
    }

    public static Double getDouble(final Object value) {
        Double ret = null;
        if (value instanceof Double) {
            ret = (Double) value;
        } else if (value instanceof Number) {
            ret = ((Number) value).doubleValue();
        } else if (value != null) {
            try {
                ret = Double.valueOf(value.toString());
            } catch (final NumberFormatException e) {
                LOGGER.log(Level.FINEST, e.getMessage(), e);
            }
        }

        return ret;
    }

    public static Time getTime(final Object value) {
        Time ret = null;
        if (value instanceof Time) {
            ret = (Time) value;
        } else if (value instanceof java.util.Date) {
            ret = new Time(((java.util.Date) value).getTime());
        } else if (value != null) {
            ret = Time.valueOf(value.toString());
        }

        return ret;
    }

    public static Timestamp getTimestamp(final Object value) {
        Timestamp ret = null;
        if (value instanceof Timestamp) {
            ret = (Timestamp) value;
        } else if (value instanceof java.util.Date) {
            ret = new Timestamp(((java.util.Date) value).getTime());
        } else if (value != null) {
            ret = Timestamp.valueOf(value.toString());
        }

        return ret;
    }

    public static Date getDate(final Object value) {
        Date ret = null;
        if (value instanceof Date) {
            ret = (Date) value;
        } else if (value != null) {
            ret = Date.valueOf(value.toString());
        }

        return ret;
    }

    public static byte[] getByteArray(final Object value) {
        byte[] ret = null;
        if (value instanceof byte[]) {
            ret = (byte[]) value;
        } else if (value != null) {
            ret = value.toString().getBytes(StandardCharsets.UTF_8);
        }

        return ret;
    }

    public static String getString(final Object value) {
        String ret = null;
        if (value instanceof String) {
            ret = (String) value;
        } else if (value instanceof byte[]) {
            ret = new String((byte[]) value, StandardCharsets.UTF_8);
        } else if (value != null) {
            ret = value.toString();
        }

        return ret;
    }
}
