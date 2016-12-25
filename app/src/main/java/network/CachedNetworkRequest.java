package network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by haseeb on 23/12/16
 */
public class CachedNetworkRequest extends JsonArrayRequest {
    protected static final int defaultClientCacheExpiry = 60 * 72;
   // protected static final int defaultClientCacheExpiry = 1 * 1;
      Context context;
      //3 days
    final String TAG = "CACHEDNETWORKREQUEST";

    public CachedNetworkRequest(int method, String url, JSONArray params,
                                Response.Listener<JSONArray> listener, Response.ErrorListener errorListener)
    {
        super(method,url, params, listener,
                errorListener);

    }

    private Priority mPriority = Priority.NORMAL;
    private String  mToken;

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        String uuid = "";
        ////Log.e(TAG, uuid);
        headers.put("X-UuidKey", uuid);
        return headers;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setHeaders(String token){
        mToken = token;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data);
            JSONArray payload = new JSONArray(jsonString);
            ////System.out.println("inside cached nw__"+jsonString);
            return Response.success(payload, enforceClientCaching(HttpHeaderParser.parseCacheHeaders(response), response));
        }catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    protected Cache.Entry enforceClientCaching(Cache.Entry entry, NetworkResponse response) {
        if (getClientCacheExpiry() == null) return entry;

        long now = System.currentTimeMillis();

        if (entry == null) {
            entry = new Cache.Entry();
            entry.data = response.data;
            entry.etag = response.headers.get("ETag");
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
            entry.serverDate = now;
            entry.responseHeaders = response.headers;
        } else if (entry.isExpired()) {
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
        }


        return entry;
    }

    protected Integer getClientCacheExpiry() {
        return defaultClientCacheExpiry;
    }

}
