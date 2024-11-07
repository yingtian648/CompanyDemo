package com.zlingsmart.demo.mtestapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.exa.baselib.utils.L;
import com.zlingsmart.demo.mtestapp.carpower.CarPowerActivity;
import com.zlingsmart.demo.mtestapp.location.LocationActivity;
import com.zlingsmart.demo.mtestapp.util.OnClickItemListener;
import com.zlingsmart.demo.mtestapp.util.Tools;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        L.dd();
        mContext = this;
        setContentView(R.layout.activity_main);
        initData();
        initListView();

        String title = "测试按钮" + " (wifi:" + Tools.INSTANCE.getWifiIp(this);
        Button testBtn = findViewById(R.id.button);
        testBtn.setText(title);
    }

    public void test(View view) {

    }

    /**
     * 添加数据
     */
    private void initData() {
        dataList = new ArrayList<>();
        dataList.add(new Pair<>("广播测试", new Pair<>(OptionActivity.class.getName(), Constants.OptionType.BROADCAST)));
        dataList.add(new Pair<>("输入法弹出", new Pair<>(TestActivity.class.getName(), Constants.OptionType.NORMAL)));
        dataList.add(new Pair<>("Location测试", new Pair<>(LocationActivity.class.getName(), Constants.OptionType.NORMAL)));
        dataList.add(new Pair<>("CarPower测试", new Pair<>(CarPowerActivity.class.getName(), Constants.OptionType.NORMAL)));
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

    @Override
    protected void onStop() {
        super.onStop();
        L.dd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.dd();
    }
}