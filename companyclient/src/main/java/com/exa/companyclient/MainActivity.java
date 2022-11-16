package com.exa.companyclient;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.exa.baselib.BaseConstants;
import com.exa.baselib.base.BaseBindActivity;
import com.exa.baselib.bean.EventBean;
import com.exa.baselib.bean.Files;
import com.exa.baselib.utils.AudioPlayerUtil;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.OnClickViewListener;
import com.exa.baselib.utils.PermissionUtil;
import com.exa.baselib.utils.Utils;
import com.exa.baselib.utils.VideoPlayer;
import com.exa.companyclient.databinding.ActivityMainBinding;
import com.exa.companyclient.provider.ExeHelper;
import com.exa.companyclient.provider.SystemMediaProviderUtil;
import com.exa.companyclient.utils.TestLock;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import gxa.car.extlocationservice.GnssHwInfo;
import gxa.car.extlocationservice.IExtiLocationInterface;

public class MainActivity extends BaseBindActivity<ActivityMainBinding> {
    private List<Files> audios = new ArrayList<>();
    private List<Files> audioThumbs = new ArrayList<>();
    private int audioThumbsIndex = 0;
    private int textClickIndex = 0;

    @Override
    protected int setContentViewLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        checkPermissions();
        registerBroadcast();
        bind.text.setMovementMethod(ScrollingMovementMethod.getInstance());
//        SystemMediaProviderUtil.registerObserver(this, SystemMediaProviderUtil.getObserver());
        bind.btn1.setOnClickListener(view -> {
//            MyProviderUtil.registerObserver(this, MyProviderUtil.getObserver());
//            SystemMediaProviderUtil.registerObserver(this, SystemMediaProviderUtil.getObserver());
            startService(new Intent(this, MyClientService.class));
//            playVideoFile();
        });
        bind.btn2.setOnClickListener(view -> {
//            MyProviderUtil.unregisterObserver(this, MyProviderUtil.getObserver());
            ExeHelper.getInstance().exeGetSystemMediaProviderData();
        });
        bind.aImage.setOnClickListener(new OnClickViewListener() {
            @Override
            public void onClickView(View v) {
                audioThumbsIndex++;
                if (audioThumbsIndex > audioThumbs.size() - 1) {
                    audioThumbsIndex = 0;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getAudioThumbToShow();
                } else {
                    showAudioThumb();
                }
            }
        });
        bind.backBtn.setOnClickListener(v -> finish());
        bind.text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        textClickIndex++;
                        Log.d("InputEventReceiver", "Text被触摸了:" + textClickIndex + " 次");
                        bind.text.setText("Text被触摸了 " + textClickIndex + " 次");
                        break;
                }
                return true;
            }
        });
        loadData();
    }

    public void playVideoFile() {
        String path = App.getContext().getFilesDir().getPath();

        //手机调试
//        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/Weixin";

        File file = new File(path);

        L.d("playVideoFile:" + path + "," + file.exists());
        if (file.exists()) {
            L.d("playVideoFile list:" + Arrays.toString(file.list()));
        }
        if (file.exists() && file.list() != null) {
            List<String> playList = new ArrayList<>();
            for (File child : Objects.requireNonNull(file.listFiles())) {
                if (!child.isDirectory() && (child.getName().toLowerCase().contains(".mp4")
                        || child.getName().toLowerCase().contains(".wmv")
                        || child.getName().toLowerCase().contains(".armvb")
                        || child.getName().toLowerCase().contains(".avi")
                        || child.getName().toLowerCase().contains(".asf"))) {
                    playList.add(child.getAbsolutePath());
                }
            }
            playVideo(playList);
        }
    }

    @Override
    protected void initData() {
        L.d("Android OS:" + Build.VERSION.RELEASE);
//        L.d("Environment root:" + Environment.getStorageDirectory());
//        Intent intent = new Intent("com.exa.companydemo.ExtLocationAidlService");
//        intent.setPackage("com.exa.companydemo");
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        Display mDisplay = getWindowManager().getDefaultDisplay();
        setText("默认显示器：" + mDisplay.getDisplayId() + ", " + mDisplay.getName() + ", " + mDisplay.isValid());
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays(null);
        if (displays != null) {
            for (int i = 0; i < displays.length; i++) {
                setText("更多显示器：" + displays[i].getDisplayId() + ", " + displays[i].getName() + ", " + displays[i].isValid());
            }
        }

        Utils.startActivityByDisplayId(this, getClass(), 5);
    }

    private void checkPermissions() {
        PermissionUtil.requestPermission(this, this::loadData,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE});
    }

    private void loadData() {
//        intentExt.setClassName("com.exa.companydemo", "com.exa.companydemo.aidlservice.ExtLocationService");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean bean) {
        if (bean.hasData()) {
            setText(bean.message + (bean.datas.size() + "  " + bean.datas.get(0).toString()));
            switch (bean.type) {
                case 1://图片
                    BaseConstants.getHandler().postDelayed(() -> {
                        for (int i = 0; i < bean.datas.size(); i++) {
                            if (Utils.isImagePath(bean.datas.get(i).path)) {
                                Glide.with(this).load(bean.datas.get(i).path).into(bind.iamge);
                                L.d("加载图片：" + bean.datas.get(i).path);
                                return;
                            }
                        }
                    }, 1000);
                    break;
                case 2://音频
                    audios.clear();
                    audioThumbs.clear();
                    BaseConstants.getHandler().postDelayed(() -> {
                        audios.addAll(bean.datas);
                        for (int i = 0; i < bean.datas.size(); i++) {
                            if (bean.datas.get(i).path != null && bean.datas.get(i).path.endsWith(".mp3")) {
                                playAudio(bean.datas.get(i).path);
                                return;
                            }
                        }
                        checkSdkToLoadThumb();
                    }, 3000);
                    break;
                case 3:
                    BaseConstants.getHandler().postDelayed(() -> {
//                        List<String> list = new ArrayList<>();
//                        for (int i = 0; i < bean.datas.size(); i++) {
//                            if (bean.datas.get(i).path != null && bean.datas.get(i).path.endsWith(".mp4")) {
//                                list.add(bean.datas.get(i).path);
//                            }
//                        }
//                        playVideo(list);
                    }, 1000);
                    break;
                case 4://歌曲封面
                    BaseConstants.getHandler().postDelayed(() -> {
                        if (!audios.isEmpty() && !bean.datas.isEmpty()) {
                            for (int j = 0; j < bean.datas.size(); j++) {
                                for (int i = 0; i < audios.size(); i++) {
                                    if (audios.get(i).album_id.equals(bean.datas.get(j).album_id)) {
                                        audios.get(i).album_path = bean.datas.get(j).album_path;
                                        audioThumbs.add(audios.get(i));
                                    }
                                }
                            }
                            updateAudioThumbs();
                        }
                    }, 1000);
                    break;
            }
        } else {
            setText(bean.message);
        }
    }

    private void checkSdkToLoadThumb() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !audios.isEmpty()) {
            BaseConstants.getFixPool().execute(() -> {
                List<String> aids = new ArrayList<>();
                for (int i = 0; i < audios.size(); i++) {
                    if (audios.get(i).album_id != null && !aids.contains(audios.get(i).album_id)) {
                        aids.add(audios.get(i).album_id);
                        audioThumbs.add(audios.get(i));
                    }
                }
                updateAudioThumbs();
            });
        }
    }

    private void updateAudioThumbs() {
        audioThumbsIndex = 0;
        if (audioThumbs.isEmpty()) {
            bind.aImage.setImageBitmap(null);
            bind.aImageName.setText("");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getAudioThumbToShow();
            } else {
                showAudioThumb();
            }
        }
    }

    private void showAudioThumb() {
        Glide.with(this).load(audioThumbs.get(audioThumbsIndex).album_path).into(bind.aImage);
        bind.aImageName.setText(audioThumbs.get(audioThumbsIndex).path);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void getAudioThumbToShow() {
        BaseConstants.getFixPool().execute(() -> {
            Bitmap bitmap = SystemMediaProviderUtil.loadAudioAlbumThumbnail(audioThumbs.get(audioThumbsIndex).album_id, audioThumbs.get(audioThumbsIndex).path);
            if (bitmap == null) {
                audioThumbsIndex++;
                if (audioThumbsIndex > audioThumbs.size() - 1) {
                    audioThumbsIndex = 0;
                } else {
                    getAudioThumbToShow();
                }
            } else {
                runOnUiThread(() -> {
                    bind.aImage.setImageBitmap(bitmap);
                    bind.aImageName.setText(audioThumbs.get(audioThumbsIndex).path);
                });
            }
        });
    }

    private void playAudio(String path) {
        L.dd(path);
        setText("playAudio:" + path);
//        path = path.replace("mnt/media_rw", "storage");
        AudioPlayerUtil.getInstance().play(this, Uri.parse(path), new AudioPlayerUtil.AudioPlayerListener() {
            @Override
            public void onStarted() {
                L.d("开始播放：歌曲");
                setText("开始播放：歌曲");
            }

            @Override
            public void onErr(String msg) {
                setText("播放歌曲失败：" + msg);
            }

            @Override
            public void onComplete() {
                L.d("歌曲 播放结束");
            }
        });
    }

    private void playVideo(List<String> paths) {
//        path = path.replace("mnt/media_rw", "storage");
        VideoPlayer.getInstance().setCallback(new VideoPlayer.Callback() {
            @Override
            public void onError(String msg) {
                L.e("playVideo onError:" + msg);
            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onComplete() {

            }
        });
        VideoPlayer.getInstance().play(this, bind.frame, paths);
    }

    private void setText(String msg) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        runOnUiThread(() -> {
            String n = msg + "\n" + bind.text.getText().toString();
            bind.text.setText(n);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissions();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            L.d("client onReceived:" + action);
            setText("client onReceived:" + action + " " + ((intent.getExtras() == null) ? "" : intent.getExtras().keySet()));
            switch (action) {
                case BaseConstants.ACTION_MY_PROVIDER_SCAN_FINISH://自定义媒体扫描完成
                    ExeHelper.getInstance().exeGetMyMediaProviderData();
                    break;
                case Intent.ACTION_MEDIA_MOUNTED://挂载
                    break;
                case Intent.ACTION_MEDIA_UNMOUNTED://卸载
                    break;
                case Intent.ACTION_MEDIA_SCANNER_STARTED://扫描开始
                    break;
                case Intent.ACTION_MEDIA_SCANNER_FINISHED://扫描结束
                    ExeHelper.getInstance().exeGetSystemMediaProviderData();
                    break;
                case Intent.ACTION_MEDIA_EJECT://拔出
                    VideoPlayer.getInstance().stop();
                    bind.iamge.setImageBitmap(null);
                    break;
            }
        }
    };

    private IExtiLocationInterface binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            L.d("onServiceConnected");
            binder = IExtiLocationInterface.Stub.asInterface(service);
            try {
                GnssHwInfo result = binder.getGnssHwInfo();
                L.d("binder.getGnssHwInfo:" + result.getVersion());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.d("onServiceDisconnected");
        }
    };

    private void registerBroadcast() {
        L.d("registerBroadcast");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addDataScheme("file");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocation(Location location) {
        setText("onLocation:" + location);
    }
}