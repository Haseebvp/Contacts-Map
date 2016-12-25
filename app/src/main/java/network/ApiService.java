package network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by haseeb on 23/12/16
 */
public class ApiService {
    private static ApiService instance;
    private static Context mCtx;
    private static Context context;
    private RequestQueue mRequestQueue;
    private static int intClearCache = 0;

    // Default maximum disk usage in bytes
    private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;
    // Default cache folder name
    private static final String DEFAULT_CACHE_DIR = "photos";
    private static ProgressDialog pd1;

    private ApiService(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            if (intClearCache == 1) {
                mRequestQueue.getCache().clear();
            }

        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(20),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }


    public static synchronized ApiService getInstance(Context context) {

        if (instance == null)
            instance = new ApiService(context);
        return instance;

    }

    public static synchronized ApiService getInstance(Context context, int intClearCache) {
        ApiService.context = context;
        ApiService.intClearCache = intClearCache;
        if (instance == null)
            instance = new ApiService(context);
        return instance;

    }

    public static synchronized ApiService getInstance(Context context, int intClearCache, ProgressDialog pd11) {
        ApiService.context = context;
        ApiService.intClearCache = intClearCache;
        ApiService.pd1 = pd11;
        if (instance == null)
            instance = new ApiService(context);
        return instance;

    }


    private RequestQueue newRequestQueue(Context context) {
        // define cache folder
        File rootCache = context.getExternalCacheDir();
        if (rootCache == null) {
            rootCache = context.getCacheDir();
        }

        File cacheDir = new File(rootCache, DEFAULT_CACHE_DIR);
        cacheDir.mkdirs();

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
        RequestQueue queue = new RequestQueue(diskBasedCache, network);
        queue.start();

        return queue;
    }


    // Services
    public void getData(final ApiCommunication listener, boolean isCached, final String SCREEN_NAME, final String url, final String flag) {
        CachedNetworkRequest jsObjRequest = new CachedNetworkRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        listener.onResponseCallback(response, flag);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        listener.onErrorCallback(error, flag);
                        getRequestQueue().getCache().remove(url);
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                return headers;
            }
        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                7000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(jsObjRequest);
        Log.e(SCREEN_NAME + "URLHIT", jsObjRequest.getUrl() + " ");
    }

}