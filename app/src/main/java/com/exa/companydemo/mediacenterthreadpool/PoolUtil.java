/*
 * Copyright (C) 2022 author cao.zhi@zlingsmart.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exa.companydemo.mediacenterthreadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The {@link PoolUtil} is a pool util .
 * need implements the abstract method .
 */
public abstract class PoolUtil<T extends Task> {

    protected ExecutorService mThreadPool;

    /**
     * Gets the thread pool policy
     * @return {@ExecutorService} the thread pool policy
     */
    protected abstract ExecutorService myExecutorService();

    /**
     * add the pool's task
     * @param task
     */
    public abstract void addTask(T task);

    protected PoolUtil(){
        mThreadPool = myExecutorService();
    }

}
