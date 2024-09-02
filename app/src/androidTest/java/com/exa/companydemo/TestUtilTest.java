package com.exa.companydemo;

import android.app.Instrumentation;
import android.content.Context;

import junit.framework.TestCase;

/**
 * @author lsh
 * @date 2024/8/29 15:38
 * @description
 */
public class TestUtilTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testReadBytes() {
    }

    public void testDoSurfaceViewAnimation() {
    }

    public void testHardKeyTest() {
    }

    public void testMockHardKeyEvent() {

    }

    public void testTestHomeKeyInterceptor() {
    }

    public void testShowToast() {
    }

    public void testStartShowAnim() {

    }

    public void testSetFull() {
        Instrumentation instrumentation = new Instrumentation();
        Context context = instrumentation.getContext();
        TestUtil.setFull(null, context, false);
    }

    public void testAlarmAfterMinute() {
    }

    public void testCopyAssetsFonts() {
    }

    public void testParsePlatformConfigFile() {
    }

    public void testSendBroadcast() {
    }

    public void testConvertGpsAndUtcTimeTest() {
    }
}