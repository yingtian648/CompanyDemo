package com.exa.baselib.bean;

import java.util.List;

public class EventBean {
    public String message;
    public List<Files> datas;
    public int type = -1;

    public boolean hasData() {
        return datas != null && datas.size() > 0;
    }

    public EventBean() {
    }

    public EventBean(String message) {
        this.message = message;
    }

    public EventBean(List<Files> datas) {
        this.datas = datas;
    }

    public EventBean(int type, List<Files> datas) {
        this.datas = datas;
        this.type = type;
    }

    public EventBean(int type, String message, List<Files> datas) {
        this.datas = datas;
        this.type = type;
        this.message = message;
    }

    public EventBean(String message, List<Files> datas) {
        this.message = message;
        this.datas = datas;
    }
}
