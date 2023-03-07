package com.exa.companydemo.utils;

import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;

import com.exa.baselib.utils.L;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static android.view.WindowManagerGlobal.ADD_OKAY;
import static android.view.WindowManagerGlobal.ADD_PERMISSION_DENIED;

/**
 * @Author lsh
 * @Date 2023/2/27 11:28
 * @Description
 */
public class PhoneManagerServiceTemp {
    private final String TAG = L.TAG;
    private static final String LAYER_MAP_FILE = "/vendor/etc/data/scene_layer_maps.xml";
    private static final String LAYER_ITEM = "item";
    private static final String WINDOW_TYPE = "window_type";
    private static final String WINDOW_LAYER = "window_layer";
    private static final String TAG_PACKAGE_NAMES = "package_names";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_SCENE_TYPE = "scene_type";
    private SparseArray<CustomerWindowType> mLayerList;

    public PhoneManagerServiceTemp() {
        mLayerList = parseSceneLayerMaps();
        L.json(new Gson().toJson(mLayerList));
        Slog.v(TAG, "mLayerList: " + mLayerList);
        int re = checkExtLayerPermission(2508, "space.syncore.cockpit.soundeffect");
        L.e("checkExtLayerPermission : " + (re == ADD_OKAY));
    }

    public int checkExtLayerPermission(int type, String packageName) {
        if (mLayerList == null) {
            Slog.w(TAG, "mLayerList is null");
            return ADD_OKAY;
        }
        CustomerWindowType cwt = mLayerList.get(type);
        if (cwt != null && cwt.packages != null && cwt.packages.contains(packageName)) {
            return ADD_OKAY;
        }
        return ADD_PERMISSION_DENIED;
    }

    private SparseArray<CustomerWindowType> parseSceneLayerMaps() {
        File configFile = new File(LAYER_MAP_FILE);
        if (!configFile.exists()) {
            Slog.d(TAG, "configFile: " + configFile.getAbsolutePath() + " isn't exist!");
            return null;
        }
        SparseArray<CustomerWindowType> results = new SparseArray();
        FileInputStream in = null;
        try {
            in = new FileInputStream(configFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            int type = 0;
            int layer = 0;
            String sceneType = null;
            ArrayList<String> packages = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // start parse file
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (LAYER_ITEM.equals(parser.getName())) {
                            packages = new ArrayList<>();
                            type = 0;
                            layer = 0;
                            sceneType = null;
                        }
                        if (WINDOW_TYPE.equals(name)) {
                            type = Integer.parseInt(parser.nextText());
                        }
                        if (WINDOW_LAYER.equals(name)) {
                            layer = Integer.parseInt(parser.nextText());
                        }
                        if (TAG_SCENE_TYPE.equals(name)) {
                            sceneType = parser.nextText();
                        }
                        if (TAG_PACKAGE.equals(name) && packages != null) {
                            packages.add(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (LAYER_ITEM.equals(parser.getName())) {
                            CustomerWindowType windowType = new CustomerWindowType();
                            windowType.sceneType = sceneType;
                            windowType.windowType = type;
                            windowType.windowLayer = layer;
                            windowType.packages = packages;
                            results.put(type, windowType);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception exp) {
            results = null;
            Slog.e(TAG, "parseLayersForTypes failed!", exp);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    class CustomerWindowType {
        String sceneType;
        int windowType;
        int windowLayer;
        ArrayList<String> packages;

        @Override
        public String toString() {
            return "CustomerWindowType{" +
                    "sceneType='" + sceneType + '\'' +
                    ", windowType=" + windowType +
                    ", windowLayer=" + windowLayer +
                    ", packages=" + packages +
                    '}';
        }
    }
}
