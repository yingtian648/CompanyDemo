package com.exa.companydemo.ford;

/**
 * @author lsh
 * @date 2024/6/24 18:22
 * @description
 */
public class FordLocationManager {

    /**
     * 回传到TCU
     * 上报偏转数据接口
     * @param pairingKey  校验key
     * @param latitudeSign  0-1 0-南纬 1-北纬
     * @param latitudeInt   0-89 纬度整数
     * @param latitudeFrac  0.000001-0.999999  纬度小数（6位小数）
     * @param LongitudeSign 0-1 0-西经 1-东经
     * @param LongitudeInt  0-179   经度整数
     * @param LongitudeFrac 0.000001-0.999999  经度小数（6位小数）
     */
    public void reportShiftedLocation(
            long pairingKey,
            int latitudeSign,
            int latitudeInt,
            float latitudeFrac,
            int LongitudeSign,
            int LongitudeInt,
            float LongitudeFrac
    ){
        //todo
    }
}
