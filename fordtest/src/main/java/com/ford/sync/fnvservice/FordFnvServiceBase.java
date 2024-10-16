/**
 * @file FordFnvServiceBase
 * @author zhengjiabo
 * @email zheng.jiabo@zlingsmart.com
 * @date 2024/8/2
 * @copyright Copyright (c) 2024 zlingsmart
 */
package com.ford.sync.fnvservice;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Provides FordFnvServiceBase
 * <p>
 * each sub service must implements FordFnvServiceBase
 **/
public interface FordFnvServiceBase {

  /**
   * service is started. All necessary initialization should be done and service should be
   * functional after this.
   */
  void init();

  /**
   * service should stop and all resources should be released.
   */
  void release();

  void dump(FileDescriptor fd, PrintWriter writer, String[] args);
}
