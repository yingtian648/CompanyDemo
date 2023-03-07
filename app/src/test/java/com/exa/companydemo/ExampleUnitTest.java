package com.exa.companydemo;

import android.location.Location;
import android.util.SparseArray;

import com.exa.companydemo.test.SonC;

import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private SparseArray<String> dumpList = new SparseArray<>();
    @Test
    public void addition_isCorrect() {
        SonC sonC = new SonC();
        System.out.println(sonC.getName());
    }
}