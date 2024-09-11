package com.ford.sync.service;

import java.io.PrintWriter;

/**
 * @author lsh
 * @date 2024/9/11 9:40
 * @description
 */
public interface FordFnvServiceBase {
    void init();

    void dump(PrintWriter writer);

    void release();
}
