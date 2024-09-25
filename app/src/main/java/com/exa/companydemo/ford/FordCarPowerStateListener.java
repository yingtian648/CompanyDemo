package com.exa.companydemo.ford;

/**
 * @author lsh
 * @date 2024/9/20 11:27
 * @description
 */
public interface FordCarPowerStateListener {
    void onStateChanged(int state);

    void onPowerOff();

    void onPopChanged(int state, int time);
}
