package com.exa.companydemo;

import android.util.SparseArray;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private SparseArray<String> dumpList = new SparseArray<>();

    public void log(String msg) {
        System.out.println(msg);
    }

    @Test
    public void addition_isCorrect() {

    }

    @Test
    public void testTreeMapSort(){
        Map<String, Object> map = new HashMap<>();
        map.put("03", "03");
        map.put("04", "04");
        map.put("01", "01");
        map.put("02", "02");
        map.put("05", "05");
        TreeMap<String, Object> treeMap = new TreeMap<>(map);
        log("---------------------");
        for ( Map.Entry<String, Object> entry : treeMap.entrySet()) {
            log(entry.getKey() + " " + entry.getValue());
        }
    }

}