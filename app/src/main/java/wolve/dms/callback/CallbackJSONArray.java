package wolve.dms.callback;

import org.json.JSONArray;

/**
 * Created by Engine on 12/26/2016.
 */
public abstract class CallbackJSONArray {

    public abstract void onResponse(JSONArray result);

    public abstract void onError(String error);
}