package com.googlecode.paradox.function.grouping;

import com.googlecode.paradox.results.ParadoxType;

/**
 * Stores a grouping value context for store temporary data.
 *
 * @version 1.0
 * @since 1.6.0
 */
public interface IGroupingContext {
    String getName();

    ParadoxType getType();
}
