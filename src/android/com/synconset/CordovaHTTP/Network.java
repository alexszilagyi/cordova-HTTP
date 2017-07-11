package com.synconset;

/**
 * Created by alexandru.szilagyi on 6/23/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Network {

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }

        return false;
    }

    public static String addQueryStringToUrlString(String url, final Map<?, ?> parameters) throws UnsupportedEncodingException {
        if (parameters == null) {
            return url;
        }

        for (Map.Entry<?, ?> parameter : parameters.entrySet()) {

            final String encodedKey = URLEncoder.encode(parameter.getKey().toString(), "UTF-8");
            String paramValue = parameter.getValue().toString();

            String[] values = paramValue.replaceAll("\\[|\\]|\\\"","").split(",");

            for (String oV: values) {

                final String encodedValue = URLEncoder.encode(oV, "UTF-8");

                if (!url.contains("?")) {
                    url += "?" + encodedKey + "=" + encodedValue;
                } else {
                    url += "&" + encodedKey + "=" + encodedValue;
                }

            }

        }

        return url;
    }
}
