package com.tretornesp.participa.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class UrlLauncher {
    public static final String HELP_URL = "https://participasalvaterra.es";
    public static final String REPORT_URL = "mailto://admin@participasalvaterra.es";

    public static void open(String url, Activity parent) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        parent.startActivity(i);
    }
}
