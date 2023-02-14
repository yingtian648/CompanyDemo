// MCallback.aidl
package com.exa.companydemo;

// Declare any non-default types here with import statements

interface MCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onChanged(in String aString);
}