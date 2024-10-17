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

    public static String getFordPowerState(int fordPowerState) {
        switch (fordPowerState) {
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_INVALID:
                return "POWER_STATE_INVALID";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_SHUTDOWN:
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
                return "POWER_STATE_STR";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_ABNORMAL:
                return "POWER_STATE_ABNORMAL";
            case FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_TRANSPORT:
                return "POWER_STATE_TRANSPORT";
        }
        return "UNKNOWN";
    }

    public static String getCarPowerState(int carPowerState) {
        switch (carPowerState) {
            case FordCarPowerManager.CarPowerStateListener.INVALID:
                return "INVALID";
            case FordCarPowerManager.CarPowerStateListener.WAIT_FOR_VHAL:
                return "WAIT_FOR_VHAL";
            case FordCarPowerManager.CarPowerStateListener.SUSPEND_ENTER:
                return "SUSPEND_ENTER";
            case FordCarPowerManager.CarPowerStateListener.SUSPEND_EXIT:
                return "SUSPEND_EXIT";
            case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_ENTER:
                return "SHUTDOWN_ENTER";
            case FordCarPowerManager.CarPowerStateListener.ON:
                return "ON";
            case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_PREPARE:
                return "SHUTDOWN_PREPARE";
            case FordCarPowerManager.CarPowerStateListener.SHUTDOWN_CANCELLED:
                return "SHUTDOWN_CANCELLED";
        }
        return "UNKNOWN";
    }
}
