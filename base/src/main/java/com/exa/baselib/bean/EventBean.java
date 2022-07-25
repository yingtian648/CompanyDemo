package com.exa.baselib.bean;

import java.util.List;

public class EventBean {
    public String message;
    public List<Files> datas;

    public boolean hasData() {
        return datas != null;
    }

    public EventBean() {
    }

    public EventBean(String message) {
        this.message = message;
    }

    public EventBean(List<Files> datas) {
        this.datas = datas;
    }

    public EventBean(String message, List<Files> datas) {
        this.message = message;
        this.datas = datas;
    }
}
