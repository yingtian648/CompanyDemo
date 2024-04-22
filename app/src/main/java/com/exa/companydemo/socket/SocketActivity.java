package com.exa.companydemo.socket;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

import com.exa.baselib.base.BaseViewBindingActivity;
import com.exa.baselib.utils.L;
import com.exa.companydemo.R;
import com.exa.companydemo.databinding.ActivitySocketBinding;
import com.exa.companydemo.socket.impl.AbstractService;
import com.exa.companydemo.utils.NetworkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.core.app.ActivityCompat;

public class SocketActivity extends BaseViewBindingActivity<ActivitySocketBinding>
        implements View.OnClickListener, AbstractService.Callback {

    private static final String TAG = "SocketActivity";
    private AbstractService socketService;

    @Override
    protected ActivitySocketBinding getViewBinding() {
        return ActivitySocketBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        checkBtPermission();
        String title = (getString(R.string.back) + " (wifi:"
                + NetworkManager.Companion.getInstance(this).getWifiIp() + ")");
        bind.toolbar.setSubtitle(title);
        bind.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private boolean checkBtPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
            }
            return false;
        } else {
            L.i(TAG, "initView: 有蓝牙连接权限");
            return true;
        }
    }

    @Override
    protected void initData() {
        bind.rg.setOnCheckedChangeListener((group, checkedId) -> {
            setTvContent("");
            if (checkedId == R.id.rbWifi) {
                socketService = AbstractService.switchServiceType(this,
                        AbstractService.TYPE_WIFI, this);
            } else {
                if (checkBtPermission()) {
                    socketService = AbstractService.switchServiceType(this,
                            AbstractService.TYPE_BLUETOOTH, this);
                }
            }
        });
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
                R.id.btnVoiceTel, R.id.btnVoiceKey, R.id.btnGps, R.id.btnVolume,
                R.id.btnUsb2, R.id.btnUsb32, R.id.btnUsb33, R.id.btnAppleConn,
                R.id.btnWifi, R.id.btnWifiSet, R.id.btnBt,
                R.id.btnBtSet, R.id.btnVersion, R.id.btnProduct,
                R.id.btnFan, R.id.btnRtc, R.id.btnTemp,
                R.id.btnNaviQuit, R.id.btnSpQuit, R.id.btnMediaQuit,
        };
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        String json = "";
        Map<String, Object> map = new HashMap<>();
        switch (v.getId()) {
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
            case R.id.btnVolume:
                map.put(ProTestParser.ParamsContent.NUM_1, "26");
                json = ProTestParser.createTestJson(ProTestParser.TestCode.FM_VOICE_VOLUME,
                        ProTestParser.MSG_TYPE_SET,
                        map);
                break;
            case R.id.btnNaviQuit:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_NAVIGATION,
                        ProTestParser.MSG_TYPE_QUIT,
                        null);
                break;
            case R.id.btnSpQuit:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_SPEECH,
                        ProTestParser.MSG_TYPE_QUIT,
                        null);
                break;
            case R.id.btnMediaQuit:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.VOICE_MEDIA,
                        ProTestParser.MSG_TYPE_QUIT,
                        null);
                break;
            case R.id.btnAppleConn:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.APPLE_CONN,
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
            case R.id.btnFan:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.CODE_623,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnRtc:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.CODE_624,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnTemp:
                json = ProTestParser.createTestJson(ProTestParser.TestCode.CODE_625,
                        ProTestParser.MSG_TYPE_REQUEST,
                        null);
                break;
            case R.id.btnBtSet:
                if (bind.etInput.getText() == null
                        || bind.etInput.getText().toString().trim().isEmpty()) {
                    return;
                }
                String con = bind.etInput.getText().toString().trim();
                map.put(ProTestParser.ParamsContent.NUM_1, con);
                json = ProTestParser.createTestJson(ProTestParser.TestCode.BLUETOOTH,
                        ProTestParser.MSG_TYPE_SET,
                        map);
                break;
        }
        sendMessage(json);
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
        if (socketService != null) {
            socketService.sendMessage(json);
        }
    }

    @Override
    public void onStarted(String port) {
        runOnUiThread(() -> setTvContent("服务端启动成功,port=" + port + ",等待客户端连接.."));
    }

    @Override
    public void onError(String msg) {
        runOnUiThread(() -> setTvContent("报错:" + msg));
    }

    @Override
    public void onClientConnected(String address, int port) {
        runOnUiThread(() -> setTvContent("Client连接:" + address + ":" + port));
    }

    @Override
    public void onReceived(String msg) {
        runOnUiThread(() -> setTvResult("客户端消息: " + msg));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socketService != null) {
            socketService.release();
        }
    }
}