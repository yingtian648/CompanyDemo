package com.exa.companydemo.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.exa.companydemo.R;

import java.util.List;

/**
 * @author  lsh
 * date 2024/3/12 11:10
 */
public class BtAdapter extends BaseAdapter {
    private final List<MBtDevice> dataList;
    private final Context context;

    public BtAdapter(Context context, List<MBtDevice> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MBtDevice dev = (MBtDevice) getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pro_test_item_bt, null);
            viewHolder.tvKey = (TextView) convertView.findViewById(R.id.tvKey);
            viewHolder.tvValue = (TextView) convertView.findViewById(R.id.tvValue);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvKey.setText(dev.getDevice().getName() + "");
        viewHolder.tvValue.setText(getBondState(dev)
                + " " + dev.getRssi()
                + " " + dev.getDevice().getAddress());
        return convertView;
    }

    static class ViewHolder {
        TextView tvKey, tvValue;
    }

    @SuppressLint("MissingPermission")
    private String getBondState(MBtDevice device) {
        if (device.isConnect()) {
            return "已连接";
        } else if (device.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
            return "已配对";
        } else {
            return "";
        }
    }
}
