/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import java.net.UnknownHostException;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class CordovaHttpGet extends CordovaHttp implements Runnable {

    public CordovaHttpGet(Context context, String urlString, Map<?, ?> params, Map<String, String> headers, CallbackContext callbackContext) {
        super(context, urlString, params, headers, callbackContext);
    }

    @Override
    public void run() {
      HttpRequest request = null;
        JSONObject response = new JSONObject();
        try {
            if (!Network.isOnline(super.context)) {
                response.put("status", ONLINE_PENDING_STATUS_CODE);
                this.getCallbackContext().error(response);
                return;
            }

            request = HttpRequest.get(this.getUrlString(), this.getParams(), true);
            CordovaHttp.addHttpRequest(request, this.getCallbackContext());

            this.setupSecurity(request);
            request.acceptCharset(CHARSET);
            request.headers(this.getHeaders());
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
        } catch (HttpRequestException e) {
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
