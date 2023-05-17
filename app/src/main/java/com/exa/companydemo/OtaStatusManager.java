package com.exa.companydemo;

/**
 * @Author lsh
 * @Date 2023/5/16 17:40
 * @Description
 */
public class OtaStatusManager {
    private static int mCurrentStatus;

    /**
     * 是否可以执行下一个流程
     * @param flow
     * @return
     */
    public static boolean checkNextFlow(int flow){
        return true;
    }

    /**
     * 完整流程
     */
    public interface FLOW{
        // 读取服务标识
        int READ_SERVICE_IDENTIFICATION = 10;
        // 权限校验
        int CHECK_PERMISSION = 20;
        // 检查下载条件
        int CHECK_DOWNLOAD_PRECONDITION = 30;
        // 开始请求下载数据
        int DOWNLOAD_START = 40;
        // 下载完成（如果是分段，则是所有分段文件下载完成）
        int DOWNLOAD_COMPLETE = 50;
        // 组包(分段下载完所有分段后执行组包操作)
        int MERGE_PACKAGE = 60;
        // 检验组包完整性
        int CHECK_MERGE_PACKAGE_INTEGRITY = 70;
        // 解密
        int DECRYPT_PACKAGE= 80;
        // 校验解密文件完整性
        int CHECK_DECRYPT_PACKAGE_INTEGRITY = 90;
        // 验签
        int VERITY_PACKAGE = 100;
        // 开始安装
        int INSTALL_START = 110;
        // 安装完成
        int INSTALL_COMPLETE = 120;
        // 版本回滚
        int ROLLBACK_VERSION = 130;
        // 版本同步
        int SYNC_VERSION = 140;
    }

    /**
     * 分段下载
     * 其中某段下载流程
     */
    public interface SEGMENTED{
        // 请求下载A段
        int REQUEST_DOWNLOAD_PIECE = 301;
        // 传输A段数据
        int TRANSFER_PIECE = 302;
        // 退出传输数据
        int TRANSFER_PIECE_EXIT = 303;
        // 校验A端数据完整性
        int CHECK_PIECE_FULL = 303;
    }
}