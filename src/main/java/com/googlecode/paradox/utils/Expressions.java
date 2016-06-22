package com.googlecode.paradox.utils;

public class Expressions {

    public static boolean accept(final String expression, final String criteria, final boolean caseSensitive) {
        final char[] crit = caseSensitive ? criteria.toCharArray() : criteria.toUpperCase().toCharArray();
        final char[] exp = caseSensitive ? expression.toCharArray() : expression.toUpperCase().toCharArray();
        final int limit = exp.length - 1;
        int index = 0;

        for (int loop = 0; loop < crit.length; loop++) {
            if (index > limit) {
                return false;
            }
            final char c = crit[loop];

            switch (c) {
            case '?':
                index++;
                break;
            case '%':
                // Has others chars
                if (loop + 1 < crit.length) {
                    final char next = crit[loop + 1];
                    while (index <= limit && next != exp[index]) {
                        index++;
                    }
                    if (index > limit || next != exp[index]) {
                        return false;
                    }
                } else {
                    return true;
                }
                break;
            default:
                if (c == exp[index]) {
                    index++;
                } else {
                    return false;
                }
                break;
            }
        }
        return index > limit;
    }

    public static boolean accept(final String expression, final String criteria) {
        return accept(expression, criteria, false);
    }

    private Expressions() {
    }
}
