/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.exa.systemui.common;

import androidx.annotation.NonNull;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Implemented by classes who want to be in:
 *   {@code adb shell dumpsys activity service com.android.systemui}
 *
 *  DumpManager
 */
public interface Dumpable {

    /**
     * Called when it's time to dump the internal state
     * @param fd A file descriptor.
     * @param pw Where to write your dump to.
     * @param args Arguments.
     */
    void dump(@NonNull FileDescriptor fd, @NonNull PrintWriter pw, @NonNull String[] args);
}
