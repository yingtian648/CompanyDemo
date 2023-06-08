package com.exa.companydemo;

public class OtaStatusManager {

    // 两个相邻流程的差值
    private static final int FLOW_STEP = 10;

    // 读取服务标识 (未使用)
    public static int FLOW_READ_SERVICE_IDENTIFICATION = 10;
    // 权限校验 (未使用)
    public static int FLOW_CHECK_PERMISSION = 20;
    //检查下载前置条件(分段下载时，每段下载前都需要检查下载条件)
    public static int FLOW_CHECK_DOWNLOAD_PRECONDITION = 30;
    // 开始请求下载数据
    public static int FLOW_DOWNLOAD_START = 40;
    // 下载完成（如果是分段，则是所有分段文件下载完成）
    public static int FLOW_DOWNLOAD_COMPLETE = 50;
    // 组包(分段下载完所有分段后执行组包操作)
    public static int FLOW_MERGE_PACKAGE = 60;
    // 检验组包完整性
    public static int FLOW_CHECK_MERGE_PACKAGE_FULL = 70;
    // 解密
    public static int FLOW_DECRYPT_PACKAGE = 80;
    // 查询解密结果
    public static int FLOW_QUERY_DECRYPT_PACKAGE_RESULT = 90;
    // 校验解密文件完整性
    public static int FLOW_CHECK_DECRYPT_PACKAGE_FULL = 100;
    // 验签
    public static int FLOW_VERITY_PACKAGE = 110;
    // 查询验签结果
    public static int FLOW_QUERY_VERITY_PACKAGE_RESULT = 120;
    // 开始安装
    public static int FLOW_INSTALL_START = 130;
    // 查询安装进度
    public static int FLOW_QUERY_INSTALL_PROGRESS = 140;
    // 安装完成
    public static int FLOW_INSTALL_COMPLETE = 150;
    // 版本同步
    public static int FLOW_SYNC_VERSION = 160;
    // 查询版本同步结果
    public static int FLOW_QUERY_SYNC_VERSION_RESULT = 170;
    // 版本回滚
    public static int FLOW_ROLLBACK_VERSION = 180;
    // 查询版本回滚结果
    public static int FLOW_QUERY_ROLLBACK_VERSION_RESULT = 190;

    private static final Object mLock = new Object();

    private static int mCurrentFlow = FLOW_CHECK_DOWNLOAD_PRECONDITION;

    /**
     * 分段下载
     * 其中某段下载流程
     */
    public interface PIECE {
        int CHECK_DOWNLOAD_PRECONDITION = 30;
        // 请求下载A段
        int REQUEST_DOWNLOAD_PIECE = 31;
        // 传输A段数据
        int TRANSFER_PIECE = 32;
        // 退出传输数据
        int TRANSFER_PIECE_EXIT = 33;
        // 校验A端数据完整性
        int CHECK_PIECE_FULL = 34;
    }

    private static OtaStatusManager mManager;

    public static OtaStatusManager getInstance() {
        synchronized (OtaStatusManager.class) {
            if (mManager == null) {
                mManager = new OtaStatusManager();
            }
        }
        return mManager;
    }

    /**
     * 是否可以执行下一个流程
     *
     * @param flow
     */
    public boolean checkNextFlow(int flow) {
        return flow >= mCurrentFlow && flow <= mCurrentFlow + FLOW_STEP;
    }

    /**
     * 设置当前流程
     *
     * @param flow
     */
    public boolean setCurrentFlow(int flow) {
        synchronized (mLock) {
            if (checkNextFlow(flow)) {
                mCurrentFlow = flow;
                return true;
            }
            return false;
        }
    }

    public int getCurrentFlow(){
        return mCurrentFlow;
    }
}