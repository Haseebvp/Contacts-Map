package network;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by haseeb on 23/12/16
 */
public interface ApiCommunication {

    void onResponseCallback(JSONArray response, String flag);
    void onErrorCallback(VolleyError error, String flag);
}