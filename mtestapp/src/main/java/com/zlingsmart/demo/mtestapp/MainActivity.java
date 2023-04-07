package com.zlingsmart.demo.mtestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.exa.baselib.BaseConstants;
import com.exa.baselib.utils.L;
import com.exa.baselib.utils.ScreenUtils;
import com.gxa.car.scene.SceneInfo;
import com.gxa.car.scene.SceneManager;
import com.gxa.car.scene.ServiceStateListener;
import com.gxa.car.scene.WindowChangeListener;
import com.zlingsmart.demo.mtestapp.util.OnClickItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity implements OnClickItemListener {
    private RecyclerView listView;
    private Context mContext;
    private List<Pair<String, Pair<String, Integer>>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        initData();
        initListView();
        test();
    }

    private void test() {
        listenWindowChangeStatus();
    }

    private void registerWindowChangedListener() {
        L.dd();
        SceneManager.getInstance(mContext).registerServiceStateListener(new ServiceStateListener() {
            @Override
            public void onServiceStarted() {
                L.dd();
                BaseConstants.getHandler().postDelayed(()->{
                    listenWindowChangeStatus();
                },3000);

            }

            @Override
            public void onServiceDied() {
                L.dd();
            }
        });

    }

    private void listenWindowChangeStatus(){
        SceneManager.getInstance(mContext).addWindowChangeListener(new WindowChangeListener() {
            @Override
            public void onWindowsChanged(SceneInfo sceneInfo, int i) {
                L.d("onWindowsChanged : " + sceneInfo.getPackageName() + ", windowType = " + sceneInfo.getWindowType());
            }

            @Override
            public void onFocusChanged(SceneInfo sceneInfo, SceneInfo sceneInfo1) {
                L.d("onFocusChanged : " + sceneInfo.getPackageName() + ", windowType = " + sceneInfo1.getWindowType());
            }
        });
    }

    /**
     * 添加数据
     */
    private void initData() {
        dataList = new ArrayList<>();
        dataList.add(new Pair<>("广播测试", new Pair<>(OptionActivity.class.getName(), Constants.OptionType.BROADCAST)));
        dataList.add(new Pair<>("输入法弹出", new Pair<>(TestActivity.class.getName(), Constants.OptionType.BROADCAST)));
    }

    private void initListView() {
        listView = findViewById(R.id.listView);
        listView.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));
        listView.setAdapter(new MAdapter());
    }

    @Override
    public void onClickItem(int position) {
        Pair<String, Pair<String, Integer>> bean = dataList.get(position);
        Intent intent = new Intent();
        intent.setClassName(this, bean.second.first);
        intent.putExtra(Constants.ExtraField.TITLE, bean.first);
        intent.putExtra(Constants.ExtraField.OPTION_TYPE, bean.second.second);
        startActivity(intent);
    }

    private class MAdapter extends RecyclerView.Adapter<MHolder> {

        @NonNull
        @Override
        public MHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_main, parent, false);
            return new MHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MHolder holder, int position) {
            holder.textView.setText(dataList.get(position).first);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

    }

    class MHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public MHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(v -> {
                onClickItem(getLayoutPosition());
            });
        }
    }
}