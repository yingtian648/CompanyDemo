package com.exa.companydemo.ford;/**
 * @Author lsh
 * @Date 2024/6/25 15:40
 * @Description
 */

/**
 * @author lsh
 * @date 2024/6/25 15:40
 * @description
 */
public interface ServiceConnectListener {
    void onServiceConnected(FNVGnssManager manager);
    void onServiceDisconnected();
}
