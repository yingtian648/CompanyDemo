package com.exa.companyclient;

import android.net.Uri;

public class Constants {
    public static String AUTHORITY = "com.media.mine";

    public static final Uri CUSTOMER_URI = Uri.parse("content://" + AUTHORITY + "/customer");
}
