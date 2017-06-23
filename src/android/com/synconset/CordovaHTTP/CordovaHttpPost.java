/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLHandshakeException;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class CordovaHttpPost extends CordovaHttp implements Runnable {
    public CordovaHttpPost(String urlString, Map<?, ?> params, Map<String, String> headers, CallbackContext callbackContext) {
        super(urlString, params, headers, callbackContext);
    }

    @Override
    public void run() {
      HttpRequest request = null;
        JSONObject response = new JSONObject();
        try {
            if (!NetworkStatus.isOnline(super.cordova.getActivity().getApplicationContext())) {
                response.put("status", ONLINE_PENDING_STATUS_CODE);
                this.getCallbackContext().error(response);
                return;
            }

            request = HttpRequest.post(this.getUrlString());
            CordovaHttp.addHttpRequest(request, this.getCallbackContext());
            
            this.setupSecurity(request);
            request.acceptCharset(CHARSET);
            request.headers(this.getHeaders());
            request.form(this.getParams());
            int code = request.code();
            String body = request.body(CHARSET);
            response.put("status", code);
            if (code >= 200 && code < 300) {
                response.put("data", body);
                this.getCallbackContext().success(response);
            } else {
                response.put("error", body);
                this.getCallbackContext().error(response);
            }
        } catch (JSONException e) {
            this.respondWithError("There was an error generating the response");
        }  catch (HttpRequestException e) {
            if (e.getCause() instanceof UnknownHostException) {
                this.respondWithError(0, "The host could not be resolved");
            } else if (e.getCause() instanceof SSLHandshakeException) {
                this.respondWithError("SSL handshake failed");
            } else {
                this.respondWithError("There was an error with the request");
            }
        }

        CordovaHttp.removeHttpRequest(request);
    }
}
