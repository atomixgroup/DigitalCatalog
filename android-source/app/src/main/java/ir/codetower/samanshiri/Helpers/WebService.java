package ir.codetower.samanshiri.Helpers;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import ir.codetower.samanshiri.App;

public class WebService {

    private StringRequest request;

    public void postRequest(String key, String value, String url, OnPostReceived onPostReceived) {
        HashMap<String, String> params = new HashMap<>();
        params.put(key, value);
        postRequest(params, url, onPostReceived);
    }

    public void postRequest(final HashMap<String, String> params, String url, final OnPostReceived onPostReceived) {
        if (App.isNetworkAvailable()) {
            request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    onPostReceived.onReceived(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onPostReceived.onReceivedError(error.getMessage());
                    if (error == null || error.networkResponse == null) {
                        return;
                    }

                    String body;
                    //get status code here
                    final String statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // exception
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }

            };
            request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(App.context).add(request);
        } else {
            onPostReceived.onReceived("E1");
        }

    }
    public void getRequest(final HashMap<String, String> params, String url, final OnPostReceived onPostReceived) {
        if (App.isNetworkAvailable()) {
            request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    onPostReceived.onReceived(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onPostReceived.onReceivedError(error.getMessage());
                    if (error == null || error.networkResponse == null) {
                        return;
                    }

                    String body;
                    //get status code here
                    final String statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // exception
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }

            };
            request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(App.context).add(request);
        } else {
            onPostReceived.onReceived("E1");
        }

    }


    public interface OnPostReceived {
        void onReceived(String message);
        void onReceivedError(String message);
    }
    public interface OnGetDataCompleteListener{
        void onGetDataCompleted(String info);
        void onGetDataError(String info);
    }
}

