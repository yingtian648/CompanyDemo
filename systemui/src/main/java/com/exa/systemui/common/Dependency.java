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

import android.app.AlarmManager;
import android.app.INotificationManager;
import android.app.IWallpaperManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.IWindowManager;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.Preconditions;
import com.exa.systemui.MCommandQueue;
import com.exa.systemui.helper.Inject;
import com.exa.systemui.helper.Lazy;
import com.exa.systemui.helper.Named;

import java.util.function.Consumer;

/**
 * Class to handle ugly dependencies throughout sysui until we determine the
 * long-term dependency injection solution.
 *
 * Classes added here should be things that are expected to live the lifetime of sysui,
 * and are generally applicable to many parts of sysui. They will be lazily
 * initialized to ensure they aren't created on form factors that don't need them
 * (e.g. HotspotController on TV). Despite being lazily initialized, it is expected
 * that all dependencies will be gotten during sysui startup, and not during runtime
 * to avoid jank.
 *
 * All classes used here are expected to manage their own lifecycle, meaning if
 * they have no clients they should not have any registered resources like bound
 * services, registered receivers, etc.
 */
public class Dependency {
    /**
     * Key for getting a the main looper.
     */
    private static final String MAIN_LOOPER_NAME = "main_looper";

    /**
     * Key for getting a background Looper for background work.
     */
    private static final String BG_LOOPER_NAME = "background_looper";
    /**
     * Key for getting a Handler for receiving time tick broadcasts on.
     */
    public static final String TIME_TICK_HANDLER_NAME = "time_tick_handler";
    /**
     * Generic handler on the main thread.
     */
    private static final String MAIN_HANDLER_NAME = "main_handler";

    /**
     * An email address to send memory leak reports to by default.
     */
    public static final String LEAK_REPORT_EMAIL_NAME = "leak_report_email";

    /**
     * Whether this platform supports long-pressing notifications to show notification channel
     * settings.
     */
    public static final String ALLOW_NOTIFICATION_LONG_PRESS_NAME = "allow_notif_longpress";

    /**
     * Key for getting a background Looper for background work.
     */
    public static final DependencyKey<Looper> BG_LOOPER = new DependencyKey<>(BG_LOOPER_NAME);
    /**
     * Key for getting a mainer Looper.
     */
    public static final DependencyKey<Looper> MAIN_LOOPER = new DependencyKey<>(MAIN_LOOPER_NAME);
    /**
     * Key for getting a Handler for receiving time tick broadcasts on.
     */
    public static final DependencyKey<Handler> TIME_TICK_HANDLER =
            new DependencyKey<>(TIME_TICK_HANDLER_NAME);
    /**
     * Generic handler on the main thread.
     */
    public static final DependencyKey<Handler> MAIN_HANDLER =
            new DependencyKey<>(MAIN_HANDLER_NAME);

    /**
     * An email address to send memory leak reports to by default.
     */
    public static final DependencyKey<String> LEAK_REPORT_EMAIL =
            new DependencyKey<>(LEAK_REPORT_EMAIL_NAME);

    private final ArrayMap<Object, Object> mDependencies = new ArrayMap<>();
    private final ArrayMap<Object, LazyDependencyCreator> mProviders = new ArrayMap<>();

    @Inject
    Lazy<NightDisplayListener> mNightDisplayListener;
    @Inject Lazy<IWindowManager> mIWindowManager;
    @Inject Lazy<IStatusBarService> mIStatusBarService;
    @Inject Lazy<DisplayMetrics> mDisplayMetrics;
    @Inject Lazy<SensorPrivacyManager> mSensorPrivacyManager;
    @Inject @Named(TIME_TICK_HANDLER_NAME) Lazy<Handler> mTimeTickHandler;
    @Inject @Named(LEAK_REPORT_EMAIL_NAME) Lazy<String> mLeakReportEmail;
    @Inject Lazy<INotificationManager> mINotificationManager;
    @Inject Lazy<AlarmManager> mAlarmManager;
    @Inject Lazy<IWallpaperManager> mWallpaperManager;
    @Inject Lazy<IWallpaperManager> mCommandQueue;

    @Inject
    public Dependency() {
    }

    /**
     * Initialize Depenency.
     */
    public void start() {
        mProviders.put(TIME_TICK_HANDLER, mTimeTickHandler::get);
        //android 31
//        mProviders.put(SensorPrivacyManager.class, mSensorPrivacyManager::get);

        mProviders.put(NightDisplayListener.class, mNightDisplayListener::get);

        mProviders.put(LEAK_REPORT_EMAIL, mLeakReportEmail::get);

        mProviders.put(IWindowManager.class, mIWindowManager::get);

        mProviders.put(IStatusBarService.class, mIStatusBarService::get);

        mProviders.put(DisplayMetrics.class, mDisplayMetrics::get);

        mProviders.put(INotificationManager.class, mINotificationManager::get);

        mProviders.put(AlarmManager.class, mAlarmManager::get);

        mProviders.put(IWallpaperManager.class, mWallpaperManager::get);
        mProviders.put(MCommandQueue.class, mCommandQueue::get);

        sDependency = this;
    }

    protected final <T> T getDependency(Class<T> cls) {
        return getDependencyInner(cls);
    }

    protected final <T> T getDependency(DependencyKey<T> key) {
        return getDependencyInner(key);
    }

    private synchronized <T> T getDependencyInner(Object key) {
        @SuppressWarnings("unchecked")
        T obj = (T) mDependencies.get(key);
        if (obj == null) {
            obj = createDependency(key);
            mDependencies.put(key, obj);

//            // TODO: Get dependencies to register themselves instead
//            if (autoRegisterModulesForDump() && obj instanceof Dumpable) {
//                mDumpManager.registerDumpable(obj.getClass().getName(), (Dumpable) obj);
//            }
        }
        return obj;
    }

    @VisibleForTesting
    protected <T> T createDependency(Object cls) {
        Preconditions.checkArgument(cls instanceof DependencyKey<?> || cls instanceof Class<?>);

        @SuppressWarnings("unchecked")
        LazyDependencyCreator<T> provider = mProviders.get(cls);
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported dependency " + cls
                    + ". " + mProviders.size() + " providers known.");
        }
        return provider.createDependency();
    }

    // Currently, there are situations in tests where we might create more than one instance of a
    // thing that should be a singleton: the "real" one (created by Dagger, usually as a result of
    // inflating a view), and a mocked one (injected into Dependency). If we register the mocked
    // one, the DumpManager will throw an exception complaining (rightly) that we have too many
    // things registered with that name. So in tests, we disable the auto-registration until the
    // root cause is fixed, i.e. inflated views in tests with Dagger dependencies.
    @VisibleForTesting
    protected boolean autoRegisterModulesForDump() {
        return true;
    }

    private static Dependency sDependency;

    /**
     * Interface for a class that can create a dependency. Used to implement laziness
     * @param <T> The type of the dependency being created
     */
    private interface LazyDependencyCreator<T> {
        T createDependency();
    }

    private <T> void destroyDependency(Class<T> cls, Consumer<T> destroy) {
        T dep = (T) mDependencies.remove(cls);
//        if (dep instanceof Dumpable) {
//            mDumpManager.unregisterDumpable(dep.getClass().getName());
//        }
        if (dep != null && destroy != null) {
            destroy.accept(dep);
        }
    }

    /**
     * Used in separate process teardown to ensure the context isn't leaked.
     *
     * TODO: Remove once PreferenceFragment doesn't reference getActivity()
     * anymore and these context hacks are no longer needed.
     */
    public static void clearDependencies() {
        sDependency = null;
    }

    /**
     * Checks to see if a dependency is instantiated, if it is it removes it from
     * the cache and calls the destroy callback.
     */
    public static <T> void destroy(Class<T> cls, Consumer<T> destroy) {
        sDependency.destroyDependency(cls, destroy);
    }

    /**
     * see docs/dagger.md
     */
    public static <T> T get(Class<T> cls) {
        return sDependency.getDependency(cls);
    }

    /**
     *  see docs/dagger.md
     */
    public static <T> T get(DependencyKey<T> cls) {
        return sDependency.getDependency(cls);
    }

    public static final class DependencyKey<V> {
        private final String mDisplayName;

        public DependencyKey(String displayName) {
            mDisplayName = displayName;
        }

        @Override
        public String toString() {
            return mDisplayName;
        }
    }

    public interface DependencyInjector {
        void createSystemUI(Dependency dependency);
    }
}
