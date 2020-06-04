package wolve.dms.models;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class Checkin extends BaseModel {
    public Checkin() {
        jsonObject = null;
    }

    public Checkin(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String CheckintoString() {
        return jsonObject.toString();
    }
}
