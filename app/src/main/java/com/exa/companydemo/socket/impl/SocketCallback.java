package com.exa.companydemo.socket.impl;

/**
 * @author  lsh
 * @date 2024/3/21 11:43
 */
public interface SocketCallback {

    /**
     * 服务端启动
     *
     * @param port 端口
     */
    void onStarted(String port);

    /**
     * 连接成功
     *
     * @param msg 消息
     */
    void onError(String msg);

    /**
     * 客户端连接
     *
     * @param address 地址
     * @param port    端口
     */
    void onClientConnected(String address, int port);

    /**
     * 接收到消息
     *
     * @param msg 消息
     */
    void onReceived(String msg);
}
