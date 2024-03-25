package com.exa.companydemo.socket;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * @author lsh
 * Date 2024/3/13 9:54
 */
public class ProTestParser {

    private static final int MAP_SIZE_SHORT = 5;
    private static final int MAP_SIZE_BIG = 12;

    private ProTestParser() {
    }

    /**
     * 消息类型-请求
     */
    public static final String MSG_TYPE_REQUEST = "request";
    /**
     * 消息类型-设置
     */
    public static final String MSG_TYPE_SET = "set";
    /**
     * 消息类型-返回
     */
    public static final String MSG_TYPE_RESPONSE = "response";
    /**
     * 消息类型-退出
     */
    public static final String MSG_TYPE_QUIT = "quit";

    /**
     * 返回内容-成功
     */
    public static final String RESPONSE_SUCCESS = "1";
    /**
     * 返回内容-失败
     */
    public static final String RESPONSE_FAILURE = "0";

    /**
     * 参数
     */
    public static class Params {
        public static final String TEST_CODE = "test-code";
        public static final String MSG_TYPE = "msg-type";

        public static final String MSG_COUNT = "msg-seq";
        public static final String PARAM_SIZE = "msg-param-size";
        public static final String PARAM_CONTENT = "msg-params";
        public static final String DEV_STATUS = "dev-status";
    }

    /**
     * 子参数内容
     */
    public static class ParamsContent {
        public static final String NUM_1 = "01";
        public static final String NUM_2 = "02";
        public static final String NUM_3 = "03";
        public static final String NUM_4 = "04";
        public static final String NUM_5 = "05";
        public static final String NUM_6 = "06";
        public static final String NUM_7 = "07";
    }

    /**
     * 测试码意义
     */
    public static class TestCode {
        public static final String VERSION = "6.5";
        public static final String PRODUCT_INFO = "6.6";

        public static final String FM = "6.7.1";
        public static final String FM_OPEN = "6.7.1.1";
        public static final String FM_SET_FRE = "6.7.1.2";
        public static final String FM_SEARCH = "6.7.7";

        public static final String FM_VOICE_VOLUME = "6.7.1.3";

        public static final String AM_OPEN = "6.7.8.1";
        public static final String AM_SET_FRE = "6.7.8.2";
        public static final String AM_SEARCH = "6.7.11";

        public static final String USB2 = "6.8.1";
        public static final String USB3_1 = "6.8.2";
        public static final String USB3_2 = "6.8.3";
        public static final String APPLE_CONN = "6.8.4";

        public static final String BLUETOOTH = "6.10.1";
        public static final String WIFI = "6.10.2";

        public static final String VOICE_NAVIGATION = "6.11.1";
        public static final String VOICE_SPEECH = "6.11.2";
        public static final String VOICE_MEDIA = "6.11.3";
        public static final String VOICE_TEL = "6.11.4";
        public static final String VOICE_KEY = "6.11.5";

        public static final String LOCATION = "6.12";
    }

    public static class DataBean {
        public String testCode;
        public String msgType;
        public String paramSize;
        public DataBeanParam paramContent;

        @Override
        public String toString() {
            return "DataBean{" +
                    "testCode='" + testCode + '\'' +
                    ", msgType='" + msgType + '\'' +
                    ", paramSize='" + paramSize + '\'' +
                    ", paramContent=" + paramContent +
                    '}';
        }
    }

    public static class DataBeanParam {
        public String num1;
        public String num2;

        @Override
        public String toString() {
            return "DataBeanParam{" +
                    "num1='" + num1 + '\'' +
                    ", num2='" + num2 + '\'' +
                    '}';
        }
    }

    /**
     * 创建测试json
     *
     * @param tessCode 测试吗
     */
    public static String createTestJson(String tessCode, String msgType
            , Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>(MAP_SIZE_SHORT);
        map.put(Params.TEST_CODE, tessCode);
        map.put(Params.MSG_TYPE, msgType);
        if (params != null && !params.isEmpty()) {
            map.put(Params.PARAM_SIZE, params.size());
            map.put(Params.PARAM_CONTENT, params);
        }
        return new Gson().toJson(map);
    }

    /**
     * 获取测试内容
     *
     * @param intent 数据来源
     */
    @Nullable
    public static DataBean getTestBean(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            String content = intent.getExtras().getString("json-key");
            return getTestBean(content);
        }
        return null;
    }

    /**
     * 获取测试内容
     *
     * @param content json字符串
     */
    @Nullable
    public static DataBean getTestBean(String content) {
        Log.d("ProTestParser", "getTestBean: " + content);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        DataBean bean = new DataBean();
        try {
            JSONObject object = new JSONObject(content);
            bean.testCode = object.getString(Params.TEST_CODE);
            bean.msgType = object.getString(Params.MSG_TYPE);
            if (object.has(Params.PARAM_SIZE)) {
                bean.paramSize = object.getString(Params.PARAM_SIZE);
            }
            if (object.has(Params.PARAM_CONTENT)) {
                JSONObject objChild = object.getJSONObject(Params.PARAM_CONTENT);
                DataBeanParam param = new DataBeanParam();
                if (objChild.has(ParamsContent.NUM_1)) {
                    param.num1 = objChild.getString(ParamsContent.NUM_1);
                }
                if (objChild.has(ParamsContent.NUM_2)) {
                    param.num2 = objChild.getString(ParamsContent.NUM_2);
                }
                bean.paramContent = param;
            }
            return bean;
        } catch (JSONException e) {
            Log.e("ProTestParser", "getTestContent: ", e);
            return null;
        }
    }

    /**
     * 获取返回json
     *
     * @param testCode 测试项目
     * @param success  是否成功
     * @return json字符串
     */
    public static String getResponse(String testCode, boolean success) {
        Map<String, Object> params = new HashMap<>(MAP_SIZE_SHORT);
        params.put(ParamsContent.NUM_1, success ? RESPONSE_SUCCESS : RESPONSE_FAILURE);
        return getResponse(testCode, params);
    }

    /**
     * 获取返回json
     *
     * @param testCode 测试项目
     * @param params   内容
     * @return json字符串
     */
    public static String getResponse(String testCode, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>(MAP_SIZE_BIG);
        map.put(Params.TEST_CODE, testCode);
        map.put(Params.MSG_TYPE, MSG_TYPE_RESPONSE);
        map.put(Params.MSG_COUNT, 10);
        map.put(Params.PARAM_SIZE, String.valueOf(params.size()));
        map.put(Params.PARAM_CONTENT, params);
        String response = new Gson().toJson(map);
        Log.d("ProTestParser", "getResponse: " + response);
        return response;
    }
}
