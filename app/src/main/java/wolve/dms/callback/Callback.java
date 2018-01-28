package wolve.dms.callback;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Engine on 12/26/2016.
 */
public abstract class Callback {

    public abstract void onResponse(JSONObject result);

    public abstract void onError(String error);
}