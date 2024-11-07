package com.zlingsmart.demo.mtestapp.carpower;

import android.car.hardware.power.FordCarPowerManager;

import java.security.PublicKey;

/**
 * @author lsh
 * @date 2024/10/17 10:03
 * @description
 */
public class CarPowerUtil {
    private CarPowerUtil() {
    }

    /**
     * 获取福特电源状态名称
     */
    public static String getFordPowerState(int fordPowerState) {
        switch (fordPowerState) {
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_INVALID:
                return "POWER_STATE_INVALID";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_STANDBY:
                return "POWER_STATE_STANDBY";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_SHUTDOWN:
                //关机
                return "POWER_STATE_SHUTDOWN";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_FUNCTIONAL:
                return "POWER_STATE_FUNCTIONAL";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_EXTENDED_PLAY:
                return "POWER_STATE_EXTENDED_PLAY";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_PHONE_MODE:
                return "POWER_STATE_PHONE_MODE";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_LOADSHED:
                return "POWER_STATE_LOADSHED";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_KOL:
                return "POWER_STATE_KOL";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_STR:
                //不应监听此状态
                return "POWER_STATE_STR";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_ABNORMAL:
                return "POWER_STATE_ABNORMAL";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_TRANSPORT:
                return "POWER_STATE_TRANSPORT";
        }
        return "UNKNOWN";
    }

    /**
     * 获取原生CarPower电源状态名称
     * STR
     */
    public static String getCarPowerState(int carPowerState) {
        switch (carPowerState) {
            case FordCarPowerManager.CarPowerStateListener.INVALID:
                return "INVALID";
            case FordCarPowerManager.CarPowerStateListener.WAIT_FOR_VHAL:
                return "WAIT_FOR_VHAL";
            case FordCarPowerManager.CarPowerStateListener.SUSPEND_ENTER:
                return "SUSPEND_ENTER";
            case FordCarPowerManager.CarPowerStateListener.SUSPEND_EXIT:
                //退出STR
                return "SUSPEND_EXIT";
            case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_ENTER:
                return "SHUTDOWN_ENTER";
            case FordCarPowerManager.CarPowerStateListener.ON:
                //全功能
                return "ON";
            case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_PREPARE:
                //进入STR
                return "SHUTDOWN_PREPARE";
            case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_CANCELLED:
                return "SHUTDOWN_CANCELLED";
        }
        return "UNKNOWN";
    }
}
