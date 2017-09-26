package wolve.dms.callback;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Engine on 12/26/2016.
 */
public abstract class Callback {
    private boolean showLoading = true;

    public boolean isShowLoading() {
        return this.showLoading;
    }

    public Callback isShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        return  this;
    }

    public abstract void onResponse(JSONObject result);


    public abstract void onError(String error);
}