package com.exa.companydemo.socket;

import android.view.View;

import com.exa.baselib.base.BaseViewBindingActivity;
import com.exa.baselib.utils.L;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivitySocketBinding;
import com.exa.companydemo.utils.NetworkManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SocketActivity extends BaseViewBindingActivity<ActivitySocketBinding> implements View.OnClickListener {
    private static final String TAG = "SocketActivity";
    private Executor mExecutor;
    private List<Socket> clientList = new ArrayList<>();
    private static final int PORT = 8080;

    @Override
    protected ActivitySocketBinding getViewBinding() {
        return ActivitySocketBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mExecutor = Executors.newFixedThreadPool(5);
        String title = (getString(R.string.back) + " (wifi:"
                + NetworkManager.Companion.getInstance(this).getWifiIp() + ")");
        bind.toolbar.setSubtitle(title);
        bind.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void initData() {
        initSocket();
        bind.btnAm.setOnClickListener(v -> {
            String json = ProTestParser.createTestJson(ProTestParser.TestCode.AM_OPEN,
                    ProTestParser.MSG_TYPE_REQUEST,
                    null);
            sendMessage(json);
        });
        bind.btnFm.setOnClickListener(v -> {
            String json = ProTestParser.createTestJson(ProTestParser.TestCode.FM_OPEN,
                    ProTestParser.MSG_TYPE_REQUEST,
                    null);
            sendMessage(json);
        });
        bind.btnFMSearch.setOnClickListener(v -> {
            String json = ProTestParser.createTestJson(ProTestParser.TestCode.FM_SEARCH,
                    ProTestParser.MSG_TYPE_REQUEST,
                    null);
            sendMessage(json);
        });
        bind.btnSetFM.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put(ProTestParser.ParamsContent.NUM_1, "98.1");
            String json = ProTestParser.createTestJson(ProTestParser.TestCode.FM_SET_FRE,
                    ProTestParser.MSG_TYPE_SET,
                    map);
            sendMessage(json);
        });
        bind.btnAmSet.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put(ProTestParser.ParamsContent.NUM_1, "981");
            String json = ProTestParser.createTestJson(ProTestParser.TestCode.AM_SET_FRE,
                    ProTestParser.MSG_TYPE_SET,
                    map);
            sendMessage(json);
        });
        bind.btnAmSearch.setOnClickListener(v -> {
            String json = ProTestParser.createTestJson(ProTestParser.TestCode.AM_SEARCH,
                    ProTestParser.MSG_TYPE_REQUEST,
                    null);
            sendMessage(json);
        });
        int[] ids = new int[]{R.id.btnVoiceGps, R.id.btnVoiceSpeech, R.id.btnVoiceMedia,
                R.id.btnVoiceTel, R.id.btnVoiceKey, R.id.btnGps,
                R.id.btnUsb2, R.id.btnUsb32,R.id.btnUsb33,
                R.id.btnWifi, R.id.btnWifiSet, R.id.btnBt,
                R.id.btnBtSet, R.id.btnVersion, R.id.btnProduct};
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        String json = "";
        Map<String,Object> map = new HashMap<>();
        switch (v.getId()){
            case R.id.btnVersion:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VERSION,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnProduct:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.PRODUCT_INFO,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnVoiceGps:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_NAVIGATION,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnVoiceSpeech:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_SPEECH,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnVoiceMedia:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_MEDIA,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnVoiceTel:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_TEL,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnVoiceKey:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_KEY,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnGps:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.LOCATION,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnUsb2:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.USB2,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnUsb32:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.USB3_2,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnUsb33:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.USB3_1,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnWifi:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.WIFI,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnWifiSet:
                map.put(ProTestParser.ParamsContent.NUM_1, "智能软件内部");
                map.put(ProTestParser.ParamsContent.NUM_2, "zmrj6666");
                json = ProTestParser.createTestJson(ProTestParser.TestCode.WIFI,
                        ProTestParser.MSG_TYPE_SET,
                        map);
                break;
            case R.id.btnBt:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.BLUETOOTH,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnBtSet:
                map.put(ProTestParser.ParamsContent.NUM_1, "OPPO K9x 5G");
                json = ProTestParser.createTestJson(ProTestParser.TestCode.BLUETOOTH,
                        ProTestParser.MSG_TYPE_SET,
                        map);
                break;
        }
        sendMessage(json);
    }

    /**
     * 初始化socket服务端
     */
    private void initSocket() {
        // 初始化socket服务端
        mExecutor.execute(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);
                setTvContent("服务端启动成功,port=" + PORT + ",等待客户端连接...");
                while (true) {
                    Socket socket = serverSocket.accept();
                    clientList.add(socket);
                    setTvContent("Client连接成功:" + socket.getInetAddress().getHostAddress() + ":"
                            + socket.getPort() + ",连接数：" + clientList.size());
                    new Thread(() -> {
                        try {
                            // 获取输入流
                            InputStream is = socket.getInputStream();
                            // 获取输出流
                            OutputStream os = socket.getOutputStream();
                            // 读取数据
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) != -1) {
                                String msg = new String(buffer, 0, len);
                                L.w("收到客户端消息: " + msg);
                                setTvResult("收到客户端消息: " + msg);
                                // 回复消息
//                                os.write("服务端收到消息".getBytes());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (IOException e) {
                L.de(e);
            }
        });
    }

    /**
     * 设置tv内容
     *
     * @param msg
     */
    private void setTvContent(String msg) {
        runOnUiThread(() -> {
            bind.tv.setText(msg);
        });
    }

    /**
     * 设置tv内容
     *
     * @param msg
     */
    private void setTvResult(String msg) {
        runOnUiThread(() -> {
            bind.tvResult.setText(msg);
        });
    }

    /**
     * 发送消息
     */
    private void sendMessage(String json) {
        L.w("sendMessage: " + json + ", clientList.size():" + clientList.size());
        mExecutor.execute(() -> {
            for (Socket socket : clientList) {
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(json.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 关闭socket
     */
    private void closeSocket() {
        mExecutor.execute(() -> {
            for (Socket socket : clientList) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化客户端socket
     * 接收服务端消息
     *
     * @param ip
     */
    private void initClientSocket(String ip) {
        mExecutor.execute(() -> {
            try {
                Socket socket = new Socket(ip, PORT);
                // 接收服务端消息
                InputStream is = socket.getInputStream();
                setTvContent("客户端连接成功:" + socket.getInetAddress().getHostAddress() + ":"
                        + socket.getPort());
                clientList.add(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}