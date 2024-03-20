package com.exa.companydemo.bluetooth;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.exa.baselib.base.BaseActivity;
import com.exa.companydemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

/**
 * @author lsh
 * 蓝牙测试
 */
public class ProTestBtActivity extends BaseActivity
        implements View.OnClickListener {
    private final String TAG = "ProTestBtActivity";
    private BluetoothUtil test;
    private ListView lvBonded, lvFind;
    private TextView tvStatus, tvConn;
    private final List<MBtDevice> bondList = new ArrayList<>();
    private final List<MBtDevice> findList = new ArrayList<>();
    private BtAdapter bondAdapter;
    private BtAdapter findAdapter;
    private String connBtName;
    private static final int TEST_TIME_OUT = 15000;

    @SuppressLint("MissingPermission")
    private void checkCurrList(String testBtName) {
        // 搜索已绑定列表设备名是否包含testBtName
        for (MBtDevice dev : bondList) {
            if (testBtName.equals(dev.getDevice().getName())) {
                test.checkToBond(dev.getDevice());
                return;
            }
        }
        // 搜索findList设备名是否包含testBtName
        for (MBtDevice dev : findList) {
            if (testBtName.equals(dev.getDevice().getName())) {
                test.checkToBond(dev.getDevice());
                return;
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pro_test_bt;
    }

    @Override
    protected void initView() {
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("蓝牙测试");

        lvBonded = findViewById(R.id.lvBonded);
        lvFind = findViewById(R.id.lvFind);
        bondAdapter = new BtAdapter(this, bondList);
        findAdapter = new BtAdapter(this, findList);
        lvBonded.setAdapter(bondAdapter);
        lvFind.setAdapter(findAdapter);
        tvStatus = findViewById(R.id.tvStatus);
        tvConn = findViewById(R.id.tvConn);
        findViewById(R.id.btnScan).setOnClickListener(this);
        findViewById(R.id.btnOpen).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnDisc).setOnClickListener(this);
        lvFind.setOnItemClickListener((parent, view, position, id) -> {
            test.checkToBond(findList.get(position).getDevice());
        });
        lvBonded.setOnItemClickListener((parent, view, position, id) -> {
            test.checkToBond(bondList.get(position).getDevice());
        });
    }

    @Override
    protected void initData() {
        test = new BluetoothUtil(this);
        test.setCallback(new BluetoothUtil.Callback() {
            @Override
            public void onStateChange(boolean enable) {
                Log.d(TAG, "onStateChange: " + enable);
                runOnUiThread(() -> {
                    tvStatus.setText("蓝牙状态：" + (enable ? "已打开" : "已关闭"));
                    if (!enable) {
                        findList.clear();
                        findAdapter.notifyDataSetChanged();
                    }
                });
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onConnectStateChange(boolean connect, MBtDevice device) {
                String name = "false";
                if (device != null && device.getDevice() != null) {
                    name = device.getDevice().getName();
                }
                runOnUiThread(() -> {
                    tvConn.setText("连接状态：" + (connect ? "已连接" : "未连接"));
                });
            }

            @Override
            public void onFail(String msg) {
                Log.d(TAG, "onFail: " + msg);
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFindDevice(MBtDevice device) {
                boolean contains = false;
                for (MBtDevice dev : bondList) {
                    if (dev.getDevice().getName() != null
                            && dev.getDevice().getName().equals(device.getDevice().getName())) {
                        contains = true;
                    }
                }
                if (!contains) {
                    for (MBtDevice dev : findList) {
                        if (dev.getDevice().getName() != null
                                && dev.getDevice().getName().equals(device.getDevice().getName())) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        findList.add(device);
                    }
                    runOnUiThread(() -> {
                        findAdapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onBondDeviceChange(List<MBtDevice> device) {
                bondList.clear();
                bondList.addAll(device);
                runOnUiThread(() -> {
                    bondAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        test.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnScan:
                test.startTest(null);
                break;
            case R.id.btnOpen:
                test.setEnable(true);
                break;
            case R.id.btnClose:
                test.setEnable(false);
                break;
            case R.id.btnDisc:
                test.disConnect();
                break;
            default:
                break;
        }
    }
}