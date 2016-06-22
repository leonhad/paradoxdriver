package com.googlecode.paradox.utils;

import java.util.List;

/**
 * Created by Andre on 25.12.2014.
 */
public interface IBlockCache {

    ClobBlock get(int num, short offset);

    void add(List<ClobBlock> blocks);

    void close();
}
