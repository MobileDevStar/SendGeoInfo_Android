package com.itdevstar.sendgeoinfo.endpoint;

import android.support.annotation.NonNull;

/**
 * Created by wayne.jackson on 6/22/16.
 */
public class EndPointUrl {
    private String url;

    public EndPointUrl(@NonNull String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
