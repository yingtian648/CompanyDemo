package com.zlingsmart.demo.mtestapp.carpower;


import android.annotation.SuppressLint;
import android.car.Car;
import android.car.VehicleAreaType;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.power.CarPowerManager;
import android.car.hardware.power.FordCarPowerManager;
import android.car.hardware.property.CarPropertyManager;
import android.view.View;

import com.exa.baselib.base.BaseViewBindingActivity;
import com.exa.baselib.utils.L;
import com.zlingsmart.demo.mtestapp.databinding.ActivityCarPowerBinding;

public class CarPowerActivity extends BaseViewBindingActivity<ActivityCarPowerBinding> {
    private FordCarPowerManager fordCarPowerManager;
    private CarPowerManager carPowerManager;
    private CarPropertyManager carPropertyManager;
    private int index = 0;
    private boolean mSetPowerOffIndicator = false;
    private Car car;

    @Override
    protected ActivityCarPowerBinding getViewBinding() {
        return ActivityCarPowerBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        L.dd();
        car = Car.createCar(this);
        initManager();
    }

    private void initManager() {
        bind.getRoot().postDelayed(() -> {
            if (car != null) {
                fordCarPowerManager = (FordCarPowerManager) car.getCarManager(Car.FORD_POWER_SERVICE);
                carPowerManager = (CarPowerManager) car.getCarManager(Car.POWER_SERVICE);
                initListener();
//                listenVhalProp();
            } else {
                L.de("car is null, delay to check!");
                setText(L.msg);
                initManager();
            }
        }, 3000);
    }

    @SuppressLint("SetTextI18n")
    private void initListener() {
        L.dd();
        setText(L.msg);
        getCurrentPowerState();
        fordCarPowerManager.setFordListener(fordListener);
        fordCarPowerManager.setListener(carPowerStateListener);
    }

    private final FordCarPowerManager.CarPowerStateListener carPowerStateListener = state -> {
        L.d("CarPowerStateListener onStateChanged:" + CarPowerUtil.getCarPowerState(state));
        setText(L.msg);
    };

    private final FordCarPowerManager.FordCarPowerStateListener fordListener = new FordCarPowerManager.FordCarPowerStateListener() {
        @Override
        public void onStateChanged(int state) {
            L.d("FordCarPowerStateListener onStateChanged:" + CarPowerUtil.getFordPowerState(state));
            setText(L.msg);
            if (state == FordCarPowerManager.FordCarPowerStateListener.POWER_STATE_LOADSHED) {
                L.d("POWER_STATE_LOADSHED, 60后 发送 loadshedShutdowm");
                setText(L.msg);
                delaySendLoadshedShutdown();
            }
        }

        @Override
        public void onPowerOff() {
            L.d("FordCarPowerStateListener onPowerOff, if mSetPowerOffIndicator is true, delay 1s send indicatePowerOff");
            setText(L.msg);
            bind.tvState.postDelayed(() -> {
                if (mSetPowerOffIndicator) {
                    fordCarPowerManager.indicatePowerOff();
                    mSetPowerOffIndicator = false;
                    L.d("indicatePowerOff");
                    setText(L.msg);
                }
            }, 1000);
        }

        @Override
        public void onPopChanged(int popState, int leftSeconds) {
            L.d("FordCarPowerStateListener onPopChanged:" + popState + ", leftSeconds" + leftSeconds);
            setText(L.msg);
        }
    };

    private void delaySendLoadshedShutdown() {
        L.dd();
        setText(L.msg);
        bind.tvState.postDelayed(() -> {
            L.dd("loadShed 60S 时间到");
            requestLoadShedShutDownNow(bind.tvState);
        }, 60000);
    }

    public void getPowerState(View v) {
        getCurrentPowerState();
    }

    public void registerFordCarPowerStateChangeListener(View view) {
        L.dd();
        setText(L.msg);
        fordCarPowerManager.setFordListener(fordListener);
    }

    public void registerCarPowerStateListener(View view) {
        L.dd();
        setText(L.msg);
        fordCarPowerManager.clearListener();
        fordCarPowerManager.setListener(carPowerStateListener);
    }

    public void requestLoadShedShutDownNow(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.requestLoadShedShutDownNow();
            setText("requestLoadShedShutDownNow");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void requestReset(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.requestReset();
            setText("requestReset");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void scheduleNextWakeupTime(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.scheduleNextWakeupTime(180);
            setText("scheduleNextWakeupTime 180s");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void setPowerOffIndicator(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.setPowerOffIndicator();
            mSetPowerOffIndicator = true;
            setText("setPowerOffIndicator");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void clearPowerOffIndicator(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.clearPowerOffIndicator();
            mSetPowerOffIndicator = false;
            setText("setPowerOffIndicator");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void syncPhoneModeStart(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.syncPhoneState(FordCarPowerManager.TELEPHONE_STATE_CALL_START);
            setText("syncPhoneModeStart");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void syncPhoneModeEnd(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.syncPhoneState(FordCarPowerManager.TELEPHONE_STATE_CALL_END);
            setText("syncPhoneModeEnd");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void setListenerWithCompletion(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            //如果之前有注册监听需要先清除监听
            fordCarPowerManager.clearListener();
            fordCarPowerManager.setListenerWithCompletion((i, completableFuture) -> {
                completableFuture.isDone();
                setText("onStateChanged:" + i);
            });
            setText("setListenerWithCompletion");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    public void clearListener(View v) {
        L.dd();
        if (fordCarPowerManager != null) {
            fordCarPowerManager.clearListener();
            setText("clearListener");
        } else {
            L.d("fordCarPowerManager is null");
            setText(L.msg);
        }
    }

    private void setText(String msg) {
        index++;
        final String content;
        if (index >= 30) {
            index = 0;
            content = msg;
        } else {
            content = bind.tvState.getText().toString() + "\n" + msg;
        }
        runOnUiThread(() -> {
            bind.tvState.setText(content);
        });
    }

    private void getCurrentPowerState() {
        String msg = "获取失败, fordCarPowerManager or carPowerManager is null";
        if (fordCarPowerManager != null && carPowerManager != null) {
            msg = "getFordPowerState:" + CarPowerUtil.getFordPowerState(fordCarPowerManager.getFordPowerState())
                    + ", getBootReason:" + fordCarPowerManager.getBootReason()
                    + ", carPowerManager.getPowerState:" + CarPowerUtil.getCarPowerState(carPowerManager.getPowerState());
        }
        L.dd(msg);
        setText(msg);
    }

    @Override
    protected void initData() {

    }

    private void listenVhalProp() {
        carPropertyManager = (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);
        carPropertyManager.registerCallback(new CarPropertyManager.CarPropertyEventCallback() {
            @Override
            public void onChangeEvent(CarPropertyValue propertyValue) {
                int prop = propertyValue.getPropertyId();
                Object value = propertyValue.getValue();
                setText("CarPropertyManager.onChangeEvent:" + prop + ", value:" + value);
            }

            @Override
            public void onErrorEvent(int i, int i1) {
                L.de("");
            }
        }, 557917440, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL);
    }
}