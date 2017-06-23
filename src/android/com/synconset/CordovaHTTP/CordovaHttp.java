/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import org.apache.cordova.CallbackContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;

import java.util.Iterator;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

public abstract class CordovaHttp {
    protected static final String TAG = "CordovaHTTP";
    protected static final String CHARSET = "UTF-8";

    private static AtomicBoolean sslPinning = new AtomicBoolean(false);
    private static AtomicBoolean acceptAllCerts = new AtomicBoolean(false);

    private static List<HttpRequest> httpRequests = new CopyOnWriteArrayList<HttpRequest>();
    private static Map<HttpRequest, CallbackContext> contextMap = new ConcurrentHashMap<HttpRequest, CallbackContext>();

    private String urlString;
    private Map<?, ?> params;
    private JSONObject jsonObject;
    private Map<String, String> headers;
    private CallbackContext callbackContext;

    public CordovaHttp(String urlString, JSONObject jsonObj, Map<String, String> headers, CallbackContext callbackContext) {
        this.urlString = urlString;
        this.jsonObject = jsonObj;
        this.headers = headers;
        this.callbackContext = callbackContext;
    }

    public CordovaHttp(String urlString, Map<?, ?> params, Map<String, String> headers, CallbackContext callbackContext) {
        this.urlString = urlString;
        this.params = params;
        this.headers = headers;
        this.callbackContext = callbackContext;
    }

    public static void enableSSLPinning(boolean enable) {
        sslPinning.set(enable);
        if (enable) {
            acceptAllCerts.set(false);
        }
    }

    public static void addHttpRequest(HttpRequest httpRequest, CallbackContext callbackContext){
      if (httpRequest == null || callbackContext == null) {
        return;
      }
      httpRequests.add(httpRequest);
      contextMap.put(httpRequest, callbackContext);
    }

    public static void removeHttpRequest(HttpRequest httpRequest){
      if (httpRequest == null) {
        return;
      }
      httpRequests.remove(httpRequest);
      contextMap.remove(httpRequest);
    }

    public static void invalidateSessionCancelingTasks(boolean cancelPendingTasks) {
        for (HttpRequest httpRequest : httpRequests) {
            System.out.println("invalidateSessionCancelingTasks reached!");
            httpRequest.invalidateSessionCancelingTasks(contextMap.get(httpRequest), cancelPendingTasks);
        }

        httpRequests.clear();
        contextMap.clear();
    }

    public static void acceptAllCerts(boolean accept) {
        acceptAllCerts.set(accept);
        if (accept) {
            sslPinning.set(false);
        }
    }

    protected String getUrlString() {
        return this.urlString;
    }

    protected JSONObject getJsonObject() {
        return this.jsonObject;
    }

    protected Map<?, ?> getParams() {
        return this.params;
    }

    protected Map<String, String> getHeaders() {
        return this.headers;
    }

    protected CallbackContext getCallbackContext() {
        return this.callbackContext;
    }

    protected HttpRequest setupSecurity(HttpRequest request) {
        if (acceptAllCerts.get()) {
            request.trustAllCerts();
            request.trustAllHosts();
        }
        if (sslPinning.get()) {
            request.pinToCerts();
        }
        return request;
    }

    protected void respondWithError(int status, String msg) {
        try {
            JSONObject response = new JSONObject();
            response.put("status", status);
            response.put("error", msg);
            this.callbackContext.error(response);
        } catch (JSONException e) {
            this.callbackContext.error(msg);
        }
    }

    protected void respondWithError(String msg) {
        this.respondWithError(500, msg);
    }
}
